/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.primarydata;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtInputDataConnection;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.primarydata.ontology.PrimaryDataMessageFromExt;
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData;
import edu.ie3.simona.api.simulation.ontology.ControlResponseMessageFromExt;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.pekko.actor.typed.ActorRef;
import org.slf4j.Logger;

/** Enables data connection of primary data between SIMONA and SimonaAPI */
public class ExtPrimaryDataConnection implements ExtInputDataConnection<PrimaryDataMessageFromExt> {

  /** Actor reference to service that handles primary data within SIMONA */
  private ActorRef<PrimaryDataMessageFromExt> dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef<ControlResponseMessageFromExt> extSimAdapter;

  /** Assets that provide primary data to SIMONA */
  private final Map<String, UUID> extPrimaryDataMapping;

  public ExtPrimaryDataConnection(Map<String, UUID> extPrimaryDataMapping) {
    this.extPrimaryDataMapping = extPrimaryDataMapping;
  }

  @Override
  public void setActorRefs(
      ActorRef<PrimaryDataMessageFromExt> dataService,
      ActorRef<ControlResponseMessageFromExt> extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

  public void convertAndSend(
      long tick, Map<String, Value> data, Optional<Long> maybeNextTick, Logger log) {
    // filtering the data and converting the keys
    Map<UUID, Value> convertedMap =
        data.entrySet().stream()
            .filter(e -> extPrimaryDataMapping.containsKey(e.getKey()))
            .collect(
                Collectors.toMap(e -> extPrimaryDataMapping.get(e.getKey()), Map.Entry::getValue));

    if (convertedMap.isEmpty()) {
      log.warn("No primary data found! Sending no primary data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with primary data.");
      providePrimaryData(tick, convertedMap, maybeNextTick);
    }
  }

  /** Returns a list of the uuids of the system participants that expect external primary data */
  public List<UUID> getPrimaryDataAssets() {
    return extPrimaryDataMapping.values().stream().toList();
  }

  /** Provide primary data from an external simulation in one tick. */
  public void providePrimaryData(
      Long tick, Map<UUID, Value> primaryData, Optional<Long> maybeNextTick) {
    sendExtMsg(new ProvidePrimaryData(tick, primaryData, maybeNextTick));
  }

  /**
   * Send information from the external simulation to SIMONA's external primary data service.
   * Furthermore, ExtSimAdapter within SIMONA is instructed to activate the ev data service with the
   * current tick.
   *
   * @param msg the data/information that is sent to SIMONA's external primary data service
   */
  public void sendExtMsg(PrimaryDataMessageFromExt msg) {
    dataService.tell(msg);
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage<>(dataService));
  }
}
