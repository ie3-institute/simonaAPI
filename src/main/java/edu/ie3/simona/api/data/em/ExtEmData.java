/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.em.ontology.EmDataMessageFromExt;
import edu.ie3.simona.api.data.em.ontology.ProvideEmData;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.primarydata.PrimaryDataFactory;
import edu.ie3.simona.api.data.primarydata.ontology.PrimaryDataMessageFromExt;
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData;
import edu.ie3.simona.api.exceptions.ConvertionException;
import org.apache.pekko.actor.ActorRef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExtEmData implements ExtData {

  /** Actor reference to service that handles ev data within SIMONA */
  private final ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private final ActorRef extSimAdapter;

  /** Factory to convert an external object to PSDM primary data */
  private final EmDataFactory factory;

  /** Assets that provide primary data to SIMONA */
  private final List<UUID> controlledEms;

  public ExtEmData(ActorRef dataService, ActorRef extSimAdapter, EmDataFactory factory, List<UUID> controlledEms) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
    this.factory = factory;
    this.controlledEms = controlledEms;
  }

  public List<UUID> getControlledEms() { return controlledEms; }

  /** Provide primary data from an external simulation in one tick. */
  public void provideEmData(Long tick, Map<String, Object> emData) {
    Map<UUID, PValue> convertedMap = new HashMap<>();
    emData.forEach(
        (k, v) -> {
          try {
            convertedMap.put(UUID.fromString(k), factory.convert(v));
          } catch (ConvertionException e) {
            throw new RuntimeException(e);
          }
        });
    sendExtMsg(new ProvideEmData(tick, convertedMap));
  }

  /**
   * Send information from the external simulation to SIMONA's external primary data service.
   * Furthermore, ExtSimAdapter within SIMONA is instructed to activate the ev data service with the
   * current tick.
   *
   * @param msg the data/information that is sent to SIMONA's external primary data service
   */
  public void sendExtMsg(EmDataMessageFromExt msg) {
    dataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
  }
}
