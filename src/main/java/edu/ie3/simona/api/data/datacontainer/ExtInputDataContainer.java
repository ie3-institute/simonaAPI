/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.datacontainer;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.em.model.FlexOptionRequestValue;
import edu.ie3.simona.api.data.em.model.FlexOptions;
import java.util.*;

/** Contains all inputs for SIMONA for a certain tick */
public final class ExtInputDataContainer implements ExtDataContainer {

  /** The tick, the input data is meant for */
  private final long tick;

  /** The next tick, when data will be provided, if available */
  private final Optional<Long> maybeNextTick;

  // primary map
  /** Map external id to primary input value for SIMONA */
  private final Map<String, Value> primaryData = new HashMap<>();

  // em maps
  private final Map<String, FlexOptionRequestValue> flexRequests = new HashMap<>();
  private final Map<String, List<FlexOptions>> flexOptions = new HashMap<>();
  private final Map<String, PValue> setPoints = new HashMap<>();

  /**
   * Container class for input data for SIMONA which can be read by SimonaAPI
   *
   * @param tick The tick, the input data is meant for
   * @param nextTick tick, when the next data will be provided
   */
  public ExtInputDataContainer(long tick, long nextTick) {
    this.tick = tick;
    this.maybeNextTick = Optional.of(nextTick);
  }

  public ExtInputDataContainer(long tick) {
    this.tick = tick;
    this.maybeNextTick = Optional.empty();
  }

  @Override
  public boolean isEmpty() {
    return primaryData.isEmpty()
        && flexRequests.isEmpty()
        && flexOptions.isEmpty()
        && setPoints.isEmpty();
  }

  public long getTick() {
    return tick;
  }

  public Optional<Long> getMaybeNextTick() {
    return maybeNextTick;
  }

  // add data
  public void addPrimaryValue(String id, Value value) {
    primaryData.put(id, value);
  }

  public void addRequest(String requester, List<String> emEntities) {
    flexRequests.put(requester, new FlexOptionRequestValue(requester, emEntities));
  }

  public void addFlexOptions(String id, FlexOptions flexOption) {
    if (!flexOptions.containsKey(id)) {
      List<FlexOptions> flexOptionValues = new ArrayList<>();
      flexOptionValues.add(flexOption);
      flexOptions.put(id, flexOptionValues);
    } else {
      flexOptions.get(id).add(flexOption);
    }
  }

  public void addSetPoint(String id, PValue setPoint) {
    setPoints.put(id, setPoint);
  }

  public Map<String, Value> extractPrimaryData() {
    return copyAndClear(primaryData);
  }

  // extract and delete data
  public Map<String, FlexOptionRequestValue> extractFlexRequests() {
    return copyAndClear(flexRequests);
  }

  public Map<String, List<FlexOptions>> extractFlexOptions() {
    return copyAndClear(flexOptions);
  }

  public Map<String, PValue> extractSetPoints() {
    return copyAndClear(setPoints);
  }

  // data to string
  public String primaryDataString() {
    return primaryData.toString();
  }

  public String flexRequestsString() {
    return flexRequests.toString();
  }

  public String flexOptionsString() {
    return flexOptions.toString();
  }

  public String setPointsString() {
    return setPoints.toString();
  }
}
