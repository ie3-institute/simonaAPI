/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.container;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.model.em.EmSetPoint;
import edu.ie3.simona.api.data.model.em.FlexOptionRequest;
import edu.ie3.simona.api.data.model.em.FlexOptions;
import java.util.*;

/** Contains all inputs for SIMONA for a certain tick */
public final class ExtInputContainer implements ExtDataContainer {

  /** The tick, the input data is meant for. */
  private final long tick;

  /** The next tick, when data will be provided, if available. */
  private final Optional<Long> maybeNextTick;

  // mapping for primary data
  /** Map uuid to primary input value for SIMONA. */
  private final Map<UUID, Value> primaryData = new HashMap<>();

  // mapping for em data
  /** Map uuid to flex option requests. */
  private final Map<UUID, FlexOptionRequest> flexRequests = new HashMap<>();

  /** Map uuid to flex options. */
  private final Map<UUID, List<FlexOptions>> flexOptions = new HashMap<>();

  /** Map uuid to em set points. */
  private final Map<UUID, EmSetPoint> setPoints = new HashMap<>();

  /**
   * Container class for input data for SIMONA which can be read by SimonaAPI
   *
   * @param tick The tick, the input data is meant for
   * @param nextTick tick, when the next data will be provided
   */
  public ExtInputContainer(long tick, long nextTick) {
    this.tick = tick;
    this.maybeNextTick = Optional.of(nextTick);
  }

  public ExtInputContainer(long tick) {
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

  /** Returns the tick the data is provided for. */
  public long getTick() {
    return tick;
  }

  /** Returns an option for the next tick, when data will be provided. */
  public Optional<Long> getMaybeNextTick() {
    return maybeNextTick;
  }

  // add data

  /**
   * Method for adding primary input values for a given asset.
   *
   * @param asset uuid, that will receive primary data
   * @param value the received value
   */
  public void addPrimaryValue(UUID asset, Value value) {
    primaryData.put(asset, value);
  }

  /**
   * Method for adding flex option requests.
   *
   * @param receiver the uuid of the agent, that will receive the request
   * @param sender option for the uuid of the sender
   */
  public void addRequest(UUID receiver, UUID sender) {
    flexRequests.put(receiver, new FlexOptionRequest(receiver, sender));
  }

  public void addRequest(UUID receiver, FlexOptionRequest request) {
    flexRequests.put(receiver, request);
  }

  /**
   * Method for adding flex options to a given receiver.
   *
   * @param receiver that will receive the flex options
   * @param flexOption that will be added
   */
  public void addFlexOptions(UUID receiver, List<FlexOptions> flexOption) {
    if (!flexOptions.containsKey(receiver)) {
      List<FlexOptions> flexOptionValues = new ArrayList<>(flexOption);
      flexOptions.put(receiver, flexOptionValues);
    } else {
      flexOptions.get(receiver).addAll(flexOption);
    }
  }

  /**
   * Method for adding an em set point for a given asset.
   *
   * @param asset that will receive the set point
   * @param power of the set point
   */
  public void addSetPoint(UUID asset, UUID sender, PValue power) {
    setPoints.put(asset, new EmSetPoint(asset, sender, power));
  }

  /**
   * Method for adding an em set point for a given asset.
   *
   * @param setPoint given set point
   */
  public void addSetPoint(EmSetPoint setPoint) {
    setPoints.put(setPoint.receiver(), setPoint);
  }

  /**
   * Extracts the primary input data from this container. All other input data remains unchanged.
   */
  public Map<UUID, Value> extractPrimaryData() {
    return copyAndClear(primaryData);
  }

  /**
   * Extracts the flex option request input data from this container. All other input data remains
   * the same.
   */
  public Map<UUID, FlexOptionRequest> extractFlexRequests() {
    return copyAndClear(flexRequests);
  }

  /**
   * Extracts the flex option input data from this container. All other input data remains the same.
   */
  public Map<UUID, List<FlexOptions>> extractFlexOptions() {
    return copyAndClear(flexOptions);
  }

  /**
   * Extracts the set point input data from this container. All other input data remains the same.
   */
  public Map<UUID, EmSetPoint> extractSetPoints() {
    return copyAndClear(setPoints);
  }

  /**
   * Returns a string representation of the primary input data without changing the data. To extract
   * (remove) the primary input data, use {@link #extractPrimaryData()} instead.
   */
  public String primaryDataString() {
    return primaryData.toString();
  }

  /**
   * Returns a string representation of the flex option request input data without changing the
   * data. To extract (remove) the flex option request input data, use {@link
   * #extractFlexRequests()} instead.
   */
  public String flexRequestsString() {
    return flexRequests.toString();
  }

  /**
   * Returns a string representation of the flex option input data without changing the data. To
   * extract (remove) the flex option input data, use {@link #extractFlexOptions()} instead.
   */
  public String flexOptionsString() {
    return flexOptions.toString();
  }

  /**
   * Returns a string representation of the set point input data without changing the data. To
   * extract (remove) the set point input data, use {@link #extractSetPoints()} instead.
   */
  public String setPointsString() {
    return setPoints.toString();
  }
}
