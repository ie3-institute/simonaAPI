/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.datamodel.models.value.Value;

import java.util.*;

/** Contains all inputs for SIMONA for a certain tick */
public class ExtInputDataContainer implements ExtDataContainer {

  private final long tick;

  /** Map external id to an input value for SIMONA */
  private final Map<String, Value> dataMap;

  private final Optional<Long> maybeNextTick;

  /**
   * Container class for input data for SIMONA
   *
   * @param tick current tick
   * @param dataMap data to be provided to SIMONA
   * @param nextTick tick, when the next data will be provided
   */
  public ExtInputDataContainer(long tick, Map<String, Value> dataMap, long nextTick) {
    this.tick = tick;
    this.dataMap = dataMap;
    this.maybeNextTick = Optional.of(nextTick);
  }

  public ExtInputDataContainer(long tick, Map<String, Value> dataMap) {
    this.tick = tick;
    this.dataMap = dataMap;
    this.maybeNextTick = Optional.empty();
  }

  public ExtInputDataContainer(long tick) {
    this(tick, new HashMap<>());
  }

  public ExtInputDataContainer(long tick, long nextTick) {
    this(tick, new HashMap<>(), nextTick);
  }

  public Map<String, Value> getSimonaInputMap() {
    return dataMap;
  }

  public Long getTick() {
    return tick;
  }

  public Optional<Long> getMaybeNextTick() {
    return maybeNextTick;
  }

  /** Adds a value to the input map */
  public void addValue(String id, Value value) {
      dataMap.put(id, value);
  }
}
