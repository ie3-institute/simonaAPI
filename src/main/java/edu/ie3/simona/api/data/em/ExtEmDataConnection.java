/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtInputDataConnection;
import edu.ie3.simona.api.data.em.ontology.EmDataMessageFromExt;
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData;
import edu.ie3.simona.api.data.ontology.DataMessageFromExt;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.simulation.ontology.ControlResponseMessageFromExt;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.pekko.actor.typed.ActorRef;
import org.slf4j.Logger;

/** Enables data connection of em data between SIMONA and SimonaAPI */
public class ExtEmDataConnection implements ExtInputDataConnection {

  /** Actor reference to service that handles ev data within SIMONA */
  private ActorRef<DataMessageFromExt> emDataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef<ControlResponseMessageFromExt> extSimAdapter;

  /** Assets that provide primary data to SIMONA */
  private final Map<String, UUID> extEmMapping;

  public ExtEmDataConnection(Map<String, UUID> extEmMapping) {
    this.extEmMapping = extEmMapping;
  }

  @Override
  public void setActorRefs(
      ActorRef<DataMessageFromExt> emDataService,
      ActorRef<ControlResponseMessageFromExt> extSimAdapter) {
    this.emDataService = emDataService;
    this.extSimAdapter = extSimAdapter;
  }

  public void convertAndSend(
      long tick, Map<String, Value> data, Optional<Long> maybeNextTick, Logger log) {
    // filtering the data and converting the keys
    Map<UUID, PValue> convertedMap =
        data.entrySet().stream()
            .filter(e -> extEmMapping.containsKey(e.getKey()))
            .collect(
                Collectors.toMap(e -> extEmMapping.get(e.getKey()), e -> (PValue) e.getValue()));

    if (convertedMap.isEmpty()) {
      log.warn("No em data found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em data.");
      provideEmData(tick, convertedMap, maybeNextTick);
    }
  }

  /** Returns a list of the uuids of the em agents that expect external set points */
  public List<UUID> getControlledEms() {
    return extEmMapping.values().stream().toList();
  }

  /** Provide primary data from an external simulation for one tick. */
  public void provideEmData(Long tick, Map<UUID, PValue> emData, Optional<Long> maybeNextTick) {
    sendExtMsg(new ProvideEmSetPointData(tick, emData, maybeNextTick));
  }

  /**
   * Send information from the external simulation to SIMONA's external primary data service.
   * Furthermore, ExtSimAdapter within SIMONA is instructed to activate the ev data service with the
   * current tick.
   *
   * @param msg the data/information that is sent to SIMONA's external primary data service
   */
  public void sendExtMsg(EmDataMessageFromExt msg) {
    emDataService.tell(msg);
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(emDataService));
  }
}
