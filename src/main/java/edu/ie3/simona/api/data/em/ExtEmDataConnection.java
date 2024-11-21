/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.ExtInputDataConnection;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ontology.EmDataMessageFromExt;
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import org.apache.pekko.actor.ActorRef;

import java.util.*;

public class ExtEmDataConnection implements ExtInputDataConnection {

  /** Actor reference to service that handles ev data within SIMONA */
  private ActorRef emDataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef extSimAdapter;

  /** Assets that provide primary data to SIMONA */
  private final Map<String, UUID> extEmMapping;

  public ExtEmDataConnection(Map<String, UUID> extEmMapping) {
    this.extEmMapping = extEmMapping;
  }

  @Override
  public void setActorRefs(ActorRef emDataService, ActorRef extSimAdapter) {
    this.emDataService = emDataService;
    this.extSimAdapter = extSimAdapter;
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
    emDataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(emDataService), ActorRef.noSender());
  }

  /** Converts an input data package from an external simulation to a map of set points */
  public Map<UUID, PValue> createExtEmDataMap(ExtInputDataContainer extInputDataContainer) {
    Map<UUID, PValue> emDataForSimona = new HashMap<>();
    extInputDataContainer
        .getSimonaInputMap()
        .forEach(
            (id, value) -> {
              if (extEmMapping.containsKey(id)) {
                if (value instanceof PValue pValue) {
                  emDataForSimona.put(extEmMapping.get(id), pValue);
                } else {
                  throw new IllegalArgumentException(
                          "EmData can only handle PValue's!"
                  );
                }
              } else {
                throw new IllegalArgumentException(
                    "Input for asset with id " + id + " was provided, but it wasn't requested!");
              }
            });
    return emDataForSimona;
  }
}
