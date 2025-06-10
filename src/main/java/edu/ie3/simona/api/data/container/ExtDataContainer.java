/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.container;

import java.util.HashMap;
import java.util.Map;

/** Interface for data that are exchanged between an external simulation and SimonaAPI */
public sealed interface ExtDataContainer permits ExtInputContainer, ExtResultContainer {

  /** Returns true, if the container is empty. */
  boolean isEmpty();

  /**
   * Method to copy a given map and clear the original.
   *
   * @param map to be copied and cleared
   * @return the copy
   * @param <K> type of key
   * @param <V> type of value
   */
  default <K, V> Map<K, V> copyAndClear(Map<K, V> map) {
    Map<K, V> result = new HashMap<>(map);
    map.clear();
    return result;
  }
}
