/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.ontology.primary.PrimaryDataMessageFromExt;
import edu.ie3.simona.api.ontology.primary.ProvidePrimaryData;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;

/** Enables data connection of primary data between SIMONA and SimonaAPI */
public final class ExtPrimaryDataConnection
    extends ExtInputDataConnection<PrimaryDataMessageFromExt> {

  private final Map<UUID, Class<? extends Value>> valueClasses;

  public ExtPrimaryDataConnection(Map<UUID, Class<? extends Value>> valueClasses) {
    this.valueClasses = valueClasses;
  }

  /** Returns a list of the uuids of the system participants that expect external primary data */
  public List<UUID> getPrimaryDataAssets() {
    return valueClasses.keySet().stream().toList();
  }

  /**
   * @param uuid of the model
   * @return an option for the value class associated with the model.
   */
  public Optional<Class<? extends Value>> getValueClass(UUID uuid) {
    return Optional.ofNullable(valueClasses.get(uuid));
  }

  /**
   * Sends primary data from an external simulation to SIMONA
   *
   * @param tick current tick
   * @param primaryData to be sent
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public void sendPrimaryData(
      long tick, Map<UUID, Value> primaryData, Optional<Long> maybeNextTick, Logger log) {
    if (primaryData.isEmpty()) {
      log.debug("No primary data found! Sending no primary data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with primary data. Data: {}", primaryData);
      sendExtMsg(new ProvidePrimaryData(tick, primaryData, maybeNextTick));
    }
  }
}
