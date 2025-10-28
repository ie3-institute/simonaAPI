/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Extended {@link FlexOptionsResult}, that contains the receiver of the flex options. This models
 * may also contain a disaggregation of the total flex options.
 */
public final class ExtendedFlexOptionsResult extends FlexOptionsResult {

  /** The disaggregated flex option results. */
  private final Map<UUID, FlexOptionsResult> disaggregated;

  /**
   * Standard constructor for {@link ExtendedFlexOptionsResult}.
   *
   * @param time date and time when the result is produced
   * @param model uuid of the input model that produces the result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   */
  public ExtendedFlexOptionsResult(
      ZonedDateTime time,
      UUID model,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(time, model, pRef, pMin, pMax);
    this.disaggregated = new HashMap<>();
  }

  /**
   * Constructor for {@link ExtendedFlexOptionsResult} with disaggregated flex options.
   *
   * @param time date and time when the result is produced
   * @param model uuid of the input model that produces the result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   * @param disaggregated a map: uuid to disaggregated flex options
   */
  public ExtendedFlexOptionsResult(
      ZonedDateTime time,
      UUID model,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax,
      Map<UUID, FlexOptions> disaggregated) {
    super(time, model, pRef, pMin, pMax);
    this.disaggregated = disaggregated;
  }

  /**
   * Method for adding disaggregated flex option results to this object.
   *
   * @param uuid of the inferior model
   * @param flexOptionsResult the flex options of the inferior model
   */
  public void addDisaggregated(UUID uuid, FlexOptionsResult flexOptionsResult) {
    this.disaggregated.put(uuid, flexOptionsResult);
  }

  /** Returns {@code true}, if disaggregated flex option are available. */
  public boolean hasDisaggregated() {
    return !disaggregated.isEmpty();
  }

  /**
   * Returns a map: uuid to disaggregated flex options.
   *
   * <p>Note: If no disaggregated flex options are present (see: {@link #hasDisaggregated()}), the
   * map will be empty.
   */
  public Map<UUID, FlexOptionsResult> getDisaggregated() {
    return Collections.unmodifiableMap(disaggregated);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExtendedFlexOptionsResult that = (ExtendedFlexOptionsResult) o;
    return Objects.equals(disaggregated, that.disaggregated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), disaggregated);
  }

  @Override
  public String toString() {
    return "ExtendedFlexOptionsResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", pRef="
        + getpRef()
        + ", pMin="
        + getpMin()
        + ", pMax="
        + getpMax()
        + ", disaggregated="
        + disaggregated
        + '}';
  }
}
