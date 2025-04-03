/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.container;

import java.util.HashMap;
import java.util.Map;

/** Interface for data that are exchanged between an external simulation and SimonaAPI */
public interface ExtDataContainer {
  boolean isEmpty();

  // private helper methods
  default <K, V> Map<K, V> copyAndClear(Map<K, V> map) {
    Map<K, V> result = new HashMap<>(map);
    map.clear();
    return result;
  }
}
