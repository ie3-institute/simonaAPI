/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.container;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.simona.api.data.model.em.EmData;
import java.util.*;

/** Contains all SIMONA results for a certain tick. */
public final class ExtOutputContainer implements ExtDataContainer {

  /** Tick for which the results are meant for. */
  private final long tick;

  /** Tick when the external simulation can expect the next results from SIMONA. */
  private final Optional<Long> maybeNextTick;

  /**
   * Map: receiver uuid to list of results from SIMONA.
   *
   * <p>ATTENTION: The time stamp of the result entities is not necessarily corresponding to the
   * tick
   */
  private final Map<UUID, List<ResultEntity>> resultMap;

  /** Map: receiver uuid to {@link EmData} from SIMONA. */
  private final Map<UUID, List<EmData>> emDataMap;

  /**
   * Container class for result data from SIMONA.
   *
   * @param tick current tick
   * @param nextTick tick the external simulation can expect the next results
   */
  public ExtOutputContainer(long tick, Optional<Long> nextTick) {
    this.tick = tick;
    this.resultMap = new HashMap<>();
    this.emDataMap = new HashMap<>();
    this.maybeNextTick = nextTick;
  }

  public ExtOutputContainer(long tick) {
    this(tick, Optional.empty());
  }

  @Override
  public boolean isEmpty() {
    return resultMap.isEmpty() && emDataMap.isEmpty();
  }

  public void addResult(UUID receiver, ResultEntity result) {
    if (resultMap.containsKey(receiver)) {
      resultMap.get(receiver).add(result);
    } else {
      List<ResultEntity> resultList = new ArrayList<>();
      resultList.add(result);
      resultMap.put(receiver, resultList);
    }
  }

  public void addResults(Map<UUID, List<ResultEntity>> result) {
    this.resultMap.putAll(result);
  }

  public void addEmData(UUID receiver, EmData emData) {
    if (emDataMap.containsKey(receiver)) {
      emDataMap.get(receiver).add(emData);
    } else {
      List<EmData> emDataList = new ArrayList<>();
      emDataList.add(emData);
      emDataMap.put(receiver, emDataList);
    }
  }

  public void addEmData(Map<UUID, List<EmData>> emData) {
    this.emDataMap.putAll(emData);
  }

  /** Returns a map: uuid to result. */
  public Map<UUID, List<ResultEntity>> getResults() {
    return resultMap;
  }

  /** Returns a map: receiver to list of {@link EmData}. */
  public Map<UUID, List<EmData>> getEmData() {
    return emDataMap;
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

    for (Map.Entry<UUID, List<ResultEntity>> entry : resultMap.entrySet()) {
      List<ResultEntity> resultEntities = entry.getValue();

      for (ResultEntity resultEntity : resultEntities) {
        if (resultEntity.getClass().equals(clazz)) {
          // add the result, if the found result is of the requested type
          result.put(entry.getKey(), (R) resultEntity);
        }
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
  public List<ResultEntity> getResult(UUID assetId) {
    return resultMap.getOrDefault(assetId, Collections.emptyList());
  }

  /** Returns the em data for a certain asset. */
  public List<EmData> getEmData(UUID assetId) {
    return emDataMap.getOrDefault(assetId, Collections.emptyList());
  }

  @Override
  public String toString() {
    return "ExtOutputContainer{"
        + "tick="
        + tick
        + ", maybeNextTick="
        + maybeNextTick
        + ", resultMap="
        + resultMap
        + ", emDataMap="
        + emDataMap
        + '}';
  }
}
