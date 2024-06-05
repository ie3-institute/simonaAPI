/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ExtInputDataPackage;
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
  private ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef extSimAdapter;

  /** Assets that provide primary data to SIMONA */
  private final Map<String, UUID> extEmMapping;

  private final EmDataFactory emDataFactory;

  public ExtEmData(
          EmDataFactory emDataFactory,
          Map<String, UUID> extEmMapping
  ) {
    this.emDataFactory = emDataFactory;
    this.extEmMapping = extEmMapping;
  }

  public void setActorRefs(
          ActorRef dataService,
          ActorRef extSimAdapter
  ) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

  public List<UUID> getControlledEms() { return extEmMapping.values().stream().toList(); }

  public EmDataFactory getEmDataFactory() {
    return emDataFactory;
  }

  /** Provide primary data from an external simulation in one tick. */
  public void provideEmData(Long tick, Map<UUID, PValue> emData) {
    sendExtMsg(new ProvideEmData(tick, emData));
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


  public Map<UUID, PValue> createExtEmDataMap(
          ExtInputDataPackage extEmData
  ) {
    Map<UUID, PValue> emDataForSimona = new HashMap<>();
    extEmData.getSimonaInputMap().forEach(
            (id, extInput) -> {
              if (extEmMapping.containsKey(id)) {
                try {
                  emDataForSimona.put(
                          extEmMapping.get(id),
                          emDataFactory.convert(extInput)
                  );
                } catch (ConvertionException e) {
                  throw new RuntimeException(e);
                }
              }
            }
    );
    return emDataForSimona;
  }
}
