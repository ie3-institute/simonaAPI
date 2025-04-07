/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.primarydata.ontology.PrimaryDataMessageFromExt;
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;

/** Enables data connection of primary data between SIMONA and SimonaAPI */
public final class ExtPrimaryDataConnection
    extends ExtInputDataConnection<PrimaryDataMessageFromExt> {

  private final Map<UUID, Class<Value>> valueClasses;

  public ExtPrimaryDataConnection(Map<UUID, Class<Value>> valueClasses) {
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
  public Optional<Class<Value>> getValueClass(UUID uuid) {
    return Optional.ofNullable(valueClasses.get(uuid));
  }

  public void sendPrimaryData(
      long tick, Map<UUID, Value> data, Optional<Long> maybeNextTick, Logger log) {
    if (data.isEmpty()) {
      log.warn("No primary data found! Sending no primary data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with primary data.");
      log.info("Data: {}", data);
      provideData(tick, data, maybeNextTick);
    }
  }

  /** Provide primary data from an external simulation in one tick. */
  public void provideData(long tick, Map<UUID, Value> primaryData, Optional<Long> maybeNextTick) {
    sendExtMsg(new ProvidePrimaryData(tick, primaryData, maybeNextTick));
  }
}
