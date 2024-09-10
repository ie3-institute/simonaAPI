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
public class ExtInputDataPackage implements ExtDataPackage {

  /** Map external id to input for SIMONA */
  private final Map<String, ExtInputDataValue> dataMap;

  private final Optional<Long> maybeNextTick;

  public ExtInputDataPackage(Map<String, ExtInputDataValue> dataMap, Optional<Long> maybeNextTick) {
    this.dataMap = dataMap;
    this.maybeNextTick = maybeNextTick;
  }

  public ExtInputDataPackage(long nextTick) {
    this(new HashMap<>(), Optional.of(nextTick));
  }

  public ExtInputDataPackage() {
    this(new HashMap<>(), Optional.empty());
  }

  public Map<String, ExtInputDataValue> getSimonaInputMap() {
    return dataMap;
  }

  public Optional<Long> getMaybeNextTick() { return maybeNextTick; }

  /** Adds a value to the input map */
  public void addValue(String id, ExtInputDataValue value) {
    dataMap.put(id, value);
  }

  public String toString() {
    return dataMap.toString();
  }
}
