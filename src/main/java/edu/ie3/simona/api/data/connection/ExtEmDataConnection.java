/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.data.model.em.EmSetPoint;
import edu.ie3.simona.api.ontology.em.EmDataMessageFromExt;
import edu.ie3.simona.api.ontology.em.EmDataResponseMessageToExt;
import edu.ie3.simona.api.ontology.em.ProvideEmSetPointData;
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

  /** Mode of the em connection */
  public enum EmMode {
    BASE,
    EM_COMMUNICATION
  }
}
