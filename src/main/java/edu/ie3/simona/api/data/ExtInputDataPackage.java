/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import java.util.HashMap;
import java.util.Map;

/** Contains all inputs for SIMONA for a certain tick */
public class ExtInputDataPackage implements ExtDataPackage {

  /** Map external id to input for SIMONA */
  private final Map<String, ExtInputDataValue> dataMap;

  public ExtInputDataPackage(Map<String, ExtInputDataValue> dataMap) {
    this.dataMap = dataMap;
  }

  public ExtInputDataPackage() {
    this(new HashMap<>());
  }

  public Map<String, ExtInputDataValue> getSimonaInputMap() {
    return dataMap;
  }

  /** Adds a value to the input map */
  public void addValue(String id, ExtInputDataValue value) {
    dataMap.put(id, value);
  }

  public String toString() {
    return dataMap.toString();
  }
}
