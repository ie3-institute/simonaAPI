/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.data.model.em.*;
import edu.ie3.simona.api.ontology.em.*;
import java.util.*;
import org.slf4j.Logger;

/** Enables data connection of em data between SIMONA and SimonaAPI */
public final class ExtEmDataConnection
    extends BiDirectional<EmDataMessageFromExt, EmDataResponseMessageToExt> {

  public final EmMode mode;

  /** Assets that are controlled by external simulation */
  private final List<UUID> controlled;

  public ExtEmDataConnection(List<UUID> controlled, EmMode mode) {
    super();

    this.mode = mode;
    this.controlled = controlled;
  }

  /** Returns a list of the uuids of the em agents that expect external set points */
  public List<UUID> getControlledEms() {
    return new ArrayList<>(controlled);
  }

  
  public void simulateInternal(long tick) {
      sendExtMsg(new EmSimulationInternal(tick));
  }

  /**
   * Tells the em service in SIMONA to simulate the given tick internally. This should be used, when
   * the external simulation will not provide data for the tick.
   *
   * @param tick that should be simulated internally
   */
  public void simulateInternal(long tick) {
    sendExtMsg(new EmSimulationInternal(tick));
  }

  /**
   * Tries to send em data to SIMONA. A message is sent, if the map is not empty.
   *
   * @param tick current tick
   * @param emData a map: receiver to em data
   * @param log logger for logging warnings
   * @return true, if data was sent
   */
  public boolean sendEmData(long tick, Map<UUID, ? extends EmData> emData, Logger log) {
    // send message only if at least one value is present
    if (!emData.isEmpty()) {
      sendExtMsg(ProvideEmData.create(tick, emData, log));
      return true;
    }
    return false;
  }

  /**
   * Tries to send em data to SIMONA. A message is sent, if at least one map is not empty.
   *
   * @param tick current tick
   * @param flexRequests receiver to flex option request
   * @param flexOptions receiver to flex options
   * @param setPoints receiver to set point that should be sent to SIMONA
   * @return true, if data was sent
   */
  public boolean sendEmData(
      long tick,
      Map<UUID, FlexOptionRequest> flexRequests,
      Map<UUID, List<FlexOptions>> flexOptions,
      Map<UUID, EmSetPoint> setPoints) {
    // send message only if at least one value is present
    if (!flexRequests.isEmpty() || !flexOptions.isEmpty() || !setPoints.isEmpty()) {
      sendExtMsg(new ProvideEmData(tick, flexRequests, flexOptions, setPoints));
      return true;
    }
    return false;
  }

  /**
   * Tries to send communication messages to SIMONA. A message is sent, if at least one message is
   * given.
   *
   * @param tick current tick
   * @param emCommunicationMessages that should be sent
   * @return true, if data was sent
   */
  public boolean sendCommunicationMessage(
      long tick, List<EmCommunicationMessage<?>> emCommunicationMessages) {
    // send message only if at least one value is present
    if (!emCommunicationMessages.isEmpty()) {
      sendExtMsg(new EmCommunicationMessages(tick, emCommunicationMessages));
      return true;
    }
    return false;
  }

  /**
   * Method to request em flexibility options from SIMONA.
   *
   * @param tick for which set points are requested
   * @param entities for which set points are requested
   * @return a map: uuid to list of flex options
   * @throws InterruptedException - on interruptions
   */
  public Map<UUID, List<FlexOptions>> requestEmFlexResults(
      long tick, List<UUID> entities, boolean disaggregated) throws InterruptedException {
    // create requests
    Map<UUID, FlexOptionRequest> requests = new HashMap<>();
    entities.forEach(
        emEntity -> requests.put(emEntity, new FlexOptionRequest(emEntity, disaggregated)));

    sendExtMsg(new ProvideEmData(tick, requests, Collections.emptyMap(), Collections.emptyMap()));
    return receiveWithType(FlexOptionsResponse.class).receiverToFlexOptions();
  }

  /**
   * Method to request the completion of the em service in SIMONA for the given tick.
   *
   * @param tick for which the em service should stop
   * @return an option for the next tick in SIMONA
   */
  public Optional<Long> requestCompletion(long tick, long nextTick) throws InterruptedException {
    sendExtMsg(new RequestEmCompletion(tick, Optional.of(nextTick)));
    return receiveWithType(EmCompletion.class).maybeNextTick();
  }

  /** Mode of the em connection */
  public enum EmMode {
    BASE,
    EM_COMMUNICATION
  }
}
