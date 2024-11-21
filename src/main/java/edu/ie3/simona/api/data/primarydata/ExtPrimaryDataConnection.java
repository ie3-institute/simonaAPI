/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.primarydata;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtInputDataConnection;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.primarydata.ontology.PrimaryDataMessageFromExt;
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData;
import org.apache.pekko.actor.ActorRef;

import java.util.*;

public class ExtPrimaryDataConnection implements ExtInputDataConnection {

  /** Actor reference to service that handles primary data within SIMONA */
  private ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef extSimAdapter;

  /** Assets that provide primary data to SIMONA */
  private final Map<String, UUID> extPrimaryDataMapping;


  public ExtPrimaryDataConnection(Map<String, UUID> extPrimaryDataMapping) {
    this.extPrimaryDataMapping = extPrimaryDataMapping;
  }

  @Override
  public void setActorRefs(ActorRef dataService, ActorRef extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
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
    dataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
  }

  /** Converts an input data package from an external simulation to a map of primary data */
  public Map<UUID, Value> convertExternalInputToPrimaryData(ExtInputDataContainer extInputDataContainer) {
    Map<UUID, Value> primaryDataForSimona = new HashMap<>();
    extInputDataContainer
        .getSimonaInputMap()
        .forEach(
            (id, value) -> {
              if (extPrimaryDataMapping.containsKey(id)) {
                primaryDataForSimona.put(extPrimaryDataMapping.get(id), value);
              } else {
                throw new IllegalArgumentException(
                    "Input for asset with id " + id + " was provided, but it wasn't requested!");
              }
            });
    return primaryDataForSimona;
  }
}
