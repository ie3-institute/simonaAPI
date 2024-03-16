/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.primarydata;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.primarydata.ontology.PrimaryDataMessageFromExt;
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData;
import edu.ie3.simona.api.exceptions.ConvertionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.pekko.actor.ActorRef;

public class ExtPrimaryData implements ExtData {

  /** Actor reference to service that handles ev data within SIMONA */
  private final ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private final ActorRef extSimAdapter;

  /** Factory to convert an external object to PSDM primary data */
  private final PrimaryDataFactory factory;

  private final List<UUID> primaryDataAssets;

  public ExtPrimaryData(ActorRef dataService, ActorRef extSimAdapter, PrimaryDataFactory factory, List<UUID> primaryDataAssets) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
    this.factory = factory;
    this.primaryDataAssets = primaryDataAssets;
  }

  public List<UUID> getPrimaryDataAssets() { return primaryDataAssets; }

  /** Provide primary data from an external simulation in one tick. */
  public void providePrimaryData(Long tick, Map<String, Object> primaryData) {
    Map<UUID, Value> convertedMap = new HashMap<>();
    primaryData.forEach(
        (k, v) -> {
          try {
            convertedMap.put(UUID.fromString(k), factory.convert(v));
          } catch (ConvertionException e) {
            throw new RuntimeException(e);
          }
        });
    sendExtMsg(new ProvidePrimaryData(tick, convertedMap));
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
}
