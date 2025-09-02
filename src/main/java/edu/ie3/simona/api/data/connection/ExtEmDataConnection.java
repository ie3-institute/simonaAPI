/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.data.model.em.EmSetPoint;
import edu.ie3.simona.api.data.model.em.ExtendedFlexOptionsResult;
import edu.ie3.simona.api.data.model.em.FlexOptionRequest;
import edu.ie3.simona.api.data.model.em.FlexOptions;
import edu.ie3.simona.api.ontology.em.*;
import java.util.*;

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

  /**
   * Tries to send em data to SIMONA. A message is sent, if at least one map is not empty.
   *
   * @param tick current tick
   * @param flexRequests receiver to flex option request
   * @param flexOptions receiver to flex options
   * @param setPoints receiver to set point
   * @param maybeNextTick option for the next tick in the simulation
   * @return true, if data was sent
   */
  public boolean sendEmData(
      long tick,
      Map<UUID, FlexOptionRequest> flexRequests,
      Map<UUID, List<FlexOptions>> flexOptions,
      Map<UUID, EmSetPoint> setPoints,
      Optional<Long> maybeNextTick) {
    // send message only if at least one value is present
    if (!flexRequests.isEmpty() || !flexOptions.isEmpty() || !setPoints.isEmpty()) {
      sendExtMsg(new ProvideEmData(tick, flexRequests, flexOptions, setPoints, maybeNextTick));
      return true;
    }
    return false;
  }

  /**
   * Tries to send the em set points to SIMONA.
   *
   * @param tick current tick
   * @param setPoints receiver to set point, that should be sent to SIMONA
   * @param maybeNextTick option for the next tick in the simulation
   * @return true, if data was sent
   */
  public boolean sendSetPoints(
      long tick, Map<UUID, EmSetPoint> setPoints, Optional<Long> maybeNextTick) {
    if (!setPoints.isEmpty()) {
      sendExtMsg(new ProvideEmSetPointData(tick, setPoints, maybeNextTick));
      return true;
    }
    return false;
  }

  /**
   * Method to request em flexibility options from SIMONA.
   *
   * @param tick for which set points are requested
   * @param emEntities for which set points are requested
   * @return an {@link FlexOptionsResponse} message
   * @throws InterruptedException - on interruptions
   */
  public Map<UUID, ExtendedFlexOptionsResult> requestEmFlexResults(
      long tick, List<UUID> emEntities, boolean disaggregated) throws InterruptedException {
    sendExtMsg(new RequestEmFlexResults(tick, emEntities, disaggregated));
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
