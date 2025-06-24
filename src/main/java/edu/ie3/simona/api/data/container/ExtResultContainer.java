/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.container;

import edu.ie3.datamodel.models.result.ResultEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Contains all SIMONA results for a certain tick. */
public final class ExtResultContainer implements ExtDataContainer {

  /** Tick for which the results are meant for. */
  private final long tick;

  /** Tick when the external simulation can expect the next results from SIMONA. */
  private final Optional<Long> maybeNextTick;

  /**
   * Map uuid to result from SIMONA.
   *
   * <p>ATTENTION: The time stamp of the result entities is not necessarily corresponding to the
   * tick
   */
  private final Map<UUID, ResultEntity> resultMap;

  /**
   * Container class for result data from SIMONA.
   *
   * @param tick current tick
   * @param resultMap results from SIMONA with external id as key
   * @param nextTick tick the external simulation can expect the next results
   */
  public ExtResultContainer(long tick, Map<UUID, ResultEntity> resultMap, Optional<Long> nextTick) {
    this.tick = tick;
    this.resultMap = resultMap;
    this.maybeNextTick = nextTick;
  }

  public ExtResultContainer(long tick, Map<UUID, ResultEntity> resultMap) {
    this(tick, resultMap, Optional.empty());
  }

  @Override
  public boolean isEmpty() {
    return resultMap.isEmpty();
  }

  /** Returns a map: uuid to result. */
  public Map<UUID, ResultEntity> getResults() {
    return resultMap;
  }

  /**
   * Method to extract results of a specific type.
   *
   * @param clazz of the results
   * @return a map: uuid to requested result, or an empty map, if no results for the requested type
   *     are present
   * @param <R> result type
   */
  @SuppressWarnings("unchecked")
  public <R extends ResultEntity> Map<UUID, R> getResults(Class<R> clazz) {
    Map<UUID, R> result = new HashMap<>();

    for (Map.Entry<UUID, ResultEntity> entry : resultMap.entrySet()) {
      ResultEntity resultEntity = entry.getValue();

      if (entry.getValue().getClass().equals(clazz)) {
        // add the result, if the found result is of the requested type
        result.put(entry.getKey(), (R) resultEntity);
      }
    }

    return result;
  }

  /** Returns the tick the data is provided for. */
  public long getTick() {
    return tick;
  }

  /** Returns an option for the next tick, when data will be provided. */
  public Optional<Long> getMaybeNextTick() {
    return maybeNextTick;
  }

  /** Returns the result for a certain asset. */
  public ResultEntity getResult(UUID assetId) {
    return resultMap.get(assetId);
  }
}
