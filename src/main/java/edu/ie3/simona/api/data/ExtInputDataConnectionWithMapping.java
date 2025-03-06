/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ontology.DataMessageFromExt;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import java.util.*;
import org.apache.pekko.actor.ActorRef;
import org.slf4j.Logger;

public abstract class ExtInputDataConnectionWithMapping<
        M extends DataMessageFromExt, V extends Value>
    implements ExtInputDataConnection<M> {

  /** Actor reference to service that handles data within SIMONA */
  private ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef extSimAdapter;

  /** Assets that provide data to SIMONA */
  protected final Map<String, UUID> extDataMapping;

  protected ExtInputDataConnectionWithMapping(Map<String, UUID> extDataMapping) {
    this.extDataMapping = extDataMapping;
  }

  @Override
  public void setActorRefs(ActorRef dataService, ActorRef extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

  /**
   * Converts the data and sends them to SIMONA.
   *
   * @param tick current tick
   * @param data to be converted and send
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public abstract void convertAndSend(
      long tick, Map<String, Value> data, Optional<Long> maybeNextTick, Logger log);

  /** Provide data from an external simulation for one tick. */
  public abstract void provideData(long tick, Map<UUID, V> data, Optional<Long> maybeNextTick);

  public void sendExtMsg(M msg) {
    dataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
  }

  /** Returns a list of the uuids of the assets that expect external data */
  protected List<UUID> getDataAssets() {
    return extDataMapping.values().stream().toList();
  }

  /**
   * Method to remap the data from the externally used {@link String} to {@link UUID} used by
   * SIMONA.
   *
   * @param inputMap map: string to value
   * @return map: uuid to value
   */
  @SuppressWarnings("unchecked")
  protected Map<UUID, V> convert(Map<String, Value> inputMap) {
    Map<UUID, V> valueMap = new HashMap<>();
    inputMap.entrySet().stream()
        .filter(e -> extDataMapping.containsKey(e.getKey()))
        .forEach(e -> valueMap.put(extDataMapping.get(e.getKey()), (V) e.getValue()));
    return valueMap;
  }
}
