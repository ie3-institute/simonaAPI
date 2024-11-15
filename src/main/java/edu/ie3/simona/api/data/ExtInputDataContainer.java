/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Contains all inputs for SIMONA for a certain tick */
public class ExtInputDataContainer implements ExtDataContainer {

  private final long tick;

  /** Map external id to input for SIMONA */
  private final Map<String, ExtInputDataValue> dataMap;

  private final Optional<Long> maybeNextTick;

  /**
   * Container class for input data for SIMONA
   *
   * @param tick current tick
   * @param dataMap data to be provided to SIMONA
   * @param maybeNextTick tick, when the next data will be provided
   */
  public ExtInputDataContainer(
      long tick, Map<String, ExtInputDataValue> dataMap, Optional<Long> maybeNextTick) {
    this.tick = tick;
    this.dataMap = dataMap;
    this.maybeNextTick = maybeNextTick;
  }

  public ExtInputDataContainer(long tick, long nextTick) {
    this(tick, new HashMap<>(), Optional.of(nextTick));
  }

  public ExtInputDataContainer(long tick) {
    this(tick, new HashMap<>(), Optional.empty());
  }

  public Map<String, ExtInputDataValue> getSimonaInputMap() {
    return dataMap;
  }

  public Long getTick() {
    return tick;
  }

  public Optional<Long> getMaybeNextTick() {
    return maybeNextTick;
  }

  /** Adds a value to the input map */
  public void addValue(String id, ExtInputDataValue value) {
    dataMap.put(id, value);
  }
}
