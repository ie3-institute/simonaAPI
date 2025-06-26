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

  /** The receiver of the message. */
  private final UUID receiver;

  /** The disaggregated flex option results. */
  private final Map<UUID, FlexOptionsResult> disaggregated;

  /**
   * Standard constructor for {@link ExtendedFlexOptionsResult}.
   *
   * @param time date and time when the result is produced
   * @param sender uuid of the input model that produces the result
   * @param receiver uuid of the receiver that will receive this result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   */
  public ExtendedFlexOptionsResult(
      ZonedDateTime time,
      UUID sender,
      UUID receiver,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(time, sender, pRef, pMin, pMax);
    this.receiver = receiver;
    this.disaggregated = new HashMap<>();
  }

  /**
   * Constructor for {@link ExtendedFlexOptionsResult} with disaggregated flex options.
   *
   * @param time date and time when the result is produced
   * @param sender uuid of the input model that produces the result
   * @param receiver uuid of the receiver that will receive this result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   */
  public ExtendedFlexOptionsResult(
      ZonedDateTime time,
      UUID sender,
      UUID receiver,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax,
      Map<UUID, FlexOptionsResult> disaggregated) {
    super(time, sender, pRef, pMin, pMax);
    this.receiver = receiver;
    this.disaggregated = disaggregated;
  }

  /** Returns the uuid of the sender ({@link #getInputModel()}) of the results. */
  public UUID getSender() {
    return getInputModel();
  }

  /** Returns the uuid of the receiver. */
  public UUID getReceiver() {
    return receiver;
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
    return Objects.equals(receiver, that.receiver)
        && Objects.equals(disaggregated, that.disaggregated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), receiver, disaggregated);
  }

  @Override
  public String toString() {
    return "ExtendedFlexOptionsResult{"
        + "time="
        + getTime()
        + ", sender="
        + getSender()
        + ", receiver="
        + receiver
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
