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

  /**
   * Sends the em flex requests to SIMONA.
   *
   * @param tick current tick
   * @param data to be sent
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public void sendFlexRequests(
      long tick, Map<UUID, FlexOptionRequest> data, Optional<Long> maybeNextTick, Logger log) {
    if (data.isEmpty()) {
      log.warn("No em flex requests found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em flex requests.");
      sendExtMsg(new ProvideFlexRequestData(tick, data, maybeNextTick));
    }
  }

  /**
   * Sends the em flex options to SIMONA.
   *
   * @param tick current tick
   * @param data to be sent
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public void sendFlexOptions(
      long tick, Map<UUID, List<FlexOptions>> data, Optional<Long> maybeNextTick, Logger log) {
    if (data.isEmpty()) {
      log.warn("No em flex options found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em flex options.");
      sendExtMsg(new ProvideEmFlexOptionData(tick, data, maybeNextTick));
    }
  }

  /**
   * Sends the em set points to SIMONA.
   *
   * @param tick current tick
   * @param setPoints to be sent
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public void sendSetPoints(
      long tick, Map<UUID, EmSetPoint> setPoints, Optional<Long> maybeNextTick, Logger log) {
    if (setPoints.isEmpty()) {
      log.debug("No em set points found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em set points.");
      sendExtMsg(new ProvideEmSetPointData(tick, setPoints, maybeNextTick));
    }
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

  public void requestCompletion(long tick) {
    sendExtMsg(new RequestEmCompletion(tick));
  }

  /** Mode of the em connection */
  public enum EmMode {
    BASE,
    EM_COMMUNICATION
  }
}
