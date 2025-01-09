/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtInputDataConnectionWithMapping;
import edu.ie3.simona.api.data.em.ontology.EmDataMessageFromExt;
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData;
import java.util.*;
import org.slf4j.Logger;

/** Enables data connection of em data between SIMONA and SimonaAPI */
public class ExtEmDataConnection
    extends ExtInputDataConnectionWithMapping<EmDataMessageFromExt, PValue> {

  public ExtEmDataConnection(Map<String, UUID> extEmMapping) {
    super(extEmMapping);
  }

  /** Returns a list of the uuids of the em agents that expect external set points */
  public List<UUID> getControlledEms() {
    return getDataAssets();
  }

  /**
   * Converts the data and sends them to SIMONA.
   *
   * @param tick current tick
   * @param data to be converted and send
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public void convertAndSend(
      long tick, Map<String, Value> data, Optional<Long> maybeNextTick, Logger log) {
    // filtering the data and converting the keys
    Map<UUID, PValue> convertedMap = convert(data);

    if (convertedMap.isEmpty()) {
      log.warn("No em data found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em data.");
      provideData(tick, convertedMap, maybeNextTick);
    }
  }

  /** Provide primary data from an external simulation for one tick. */
  public void provideData(long tick, Map<UUID, PValue> emData, Optional<Long> maybeNextTick) {
    sendExtMsg(new ProvideEmSetPointData(tick, emData, maybeNextTick));
  }
}
