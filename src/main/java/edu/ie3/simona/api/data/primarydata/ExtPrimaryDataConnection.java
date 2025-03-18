/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.primarydata;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtInputDataConnectionWithMapping;
import edu.ie3.simona.api.data.primarydata.ontology.PrimaryDataMessageFromExt;
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData;
import edu.ie3.simona.api.simulation.mapping.ExtEntityMapping;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;

/** Enables data connection of primary data between SIMONA and SimonaAPI */
public class ExtPrimaryDataConnection
    extends ExtInputDataConnectionWithMapping<PrimaryDataMessageFromExt, Value> {

  private Map<UUID, Class<Value>> valueClasses;

  public ExtPrimaryDataConnection(Map<String, UUID> extPrimaryDataMapping) {
    super(extPrimaryDataMapping);
  }

  public void setValueClasses(Map<UUID, Class<Value>> valueClasses) {
    this.valueClasses = valueClasses;
  }

  /** Returns a list of the uuids of the system participants that expect external primary data */
  public List<UUID> getPrimaryDataAssets() {
    return getDataAssets();
  }

  /**
   * @param uuid of the model
   * @return an option for the value class associated with the model.
   */
  public Optional<Class<Value>> getValueClass(UUID uuid) {
    return Optional.ofNullable(valueClasses.get(uuid));
  }

  @Override
  public void convertAndSend(
      long tick, Map<String, Value> data, Optional<Long> maybeNextTick, Logger log) {
    // filtering the data and converting the keys
    Map<UUID, Value> convertedMap = ExtEntityMapping.mapToSimona(data, extDataMapping);

    if (convertedMap.isEmpty()) {
      log.warn("No primary data found! Sending no primary data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with primary data.");
      log.info("Data: {}", convertedMap);
      provideData(tick, convertedMap, maybeNextTick);
    }
  }

  /** Provide primary data from an external simulation in one tick. */
  @Override
  public void provideData(long tick, Map<UUID, Value> primaryData, Optional<Long> maybeNextTick) {
    sendExtMsg(new ProvidePrimaryData(tick, primaryData, maybeNextTick));
  }
}
