/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ExtInputDataPackage;
import edu.ie3.simona.api.data.em.ontology.EmDataMessageFromExt;
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.exceptions.ConvertionException;

import java.util.*;

import org.apache.pekko.actor.ActorRef;

public class ExtEmData implements ExtData {

  /** Actor reference to service that handles ev data within SIMONA */
  private ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef extSimAdapter;

  /** Assets that provide primary data to SIMONA */
  private final Map<String, UUID> extEmMapping;

  /** Factory, that converts external input data to set points for EM agents */
  private final EmDataFactory emDataFactory;

  public ExtEmData(EmDataFactory emDataFactory, Map<String, UUID> extEmMapping) {
    this.emDataFactory = emDataFactory;
    this.extEmMapping = extEmMapping;
  }

  /** Sets the actor refs for data and control flow */
  public void setActorRefs(ActorRef dataService, ActorRef extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

  /** Returns a list of the uuids of the em agents that expect external set points */
  public List<UUID> getControlledEms() {
    return extEmMapping.values().stream().toList();
  }

  public EmDataFactory getEmDataFactory() {
    return emDataFactory;
  }

  /** Provide primary data from an external simulation in one tick. */
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
    dataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
  }

  /** Converts an input data package from an external simulation to a map of set points */
  public Map<UUID, PValue> createExtEmDataMap(ExtInputDataPackage extInputDataPackage) {
    Map<UUID, PValue> emDataForSimona = new HashMap<>();
    extInputDataPackage
        .getSimonaInputMap()
        .forEach(
            (id, extInput) -> {
              if (extEmMapping.containsKey(id)) {
                try {
                  emDataForSimona.put(extEmMapping.get(id), emDataFactory.convert(extInput));
                } catch (ConvertionException e) {
                  throw new RuntimeException(e);
                }
              } else {
                throw new IllegalArgumentException(
                    "Input for asset with id " + id + " was provided, but it wasn't requested!");
              }
            });
    return emDataForSimona;
  }
}
