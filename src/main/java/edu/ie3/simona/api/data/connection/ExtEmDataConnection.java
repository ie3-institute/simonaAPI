/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.em.ontology.EmDataMessageFromExt;
import edu.ie3.simona.api.data.em.ontology.EmDataResponseMessageToExt;
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData;
import edu.ie3.simona.api.mapping.DataType;
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
   * @param data to be sent
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public void sendSetPoints(
      long tick, Map<UUID, PValue> data, Optional<Long> maybeNextTick, Logger log) {
    if (data.isEmpty()) {
      log.warn("No em set points found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em set points.");
      sendExtMsg(new ProvideEmSetPointData(tick, data, maybeNextTick));
    }
  }

  /** Mode of the em connection */
  public enum EmMode {
    SET_POINT("setPoint"),
    EM_COMMUNICATION("emCommunication"),
    EM_OPTIMIZATION("emOptimization");

    public final String mode;

    EmMode(String mode) {
      this.mode = mode;
    }

    /**
     * Method to get the {@link EmMode} from the em {@link DataType}.
     *
     * @param dataType given data type
     * @return an {@link EmMode}, or throws an exception if no mode is found for the provided data
     *     type
     */
    public static EmMode fromDataType(DataType dataType) {
      return switch (dataType) {
        case EXT_EM_INPUT -> EmMode.SET_POINT;
        case EXT_EM_COMMUNICATION -> EmMode.EM_COMMUNICATION;
        case EXT_EM_OPTIMIZER -> EmMode.EM_OPTIMIZATION;
        default -> throw new IllegalStateException("Unexpected data type: " + dataType);
      };
    }
  }
}
