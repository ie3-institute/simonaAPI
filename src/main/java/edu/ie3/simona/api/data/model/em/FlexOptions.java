/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import tech.units.indriya.ComparableQuantity;

/** Flex option that will be sent to SIMONA. */
public final class FlexOptions extends EmMessageBase {

  /** The sender of the request. */
  public final UUID sender;

  /** Active power (might be negative, thus feed-in) that was suggested for regular usage. */
  public final ComparableQuantity<Power> pRef;

  /**
   * Minimal active power to which the sender can be reduced (might be negative, thus feed-in), that
   * was determined by the system. Therefore equates to lower bound of possible flexibility
   * provision.
   */
  public final ComparableQuantity<Power> pMin;

  /**
   * Maximum active power to which the sender can be increased (might be negative, thus feed-in),
   * that was determined by the system. Therefore equates to upper bound of possible flexibility
   * provision.
   */
  public final ComparableQuantity<Power> pMax;

  /**
   * Flex option that will be sent to SIMONA.
   *
   * @param receiver uuid of the flex options
   * @param sender uuid of the flex options
   * @param pRef current active power
   * @param pMin minimal active power
   * @param pMax maximal active power
   */
  public FlexOptions(
      UUID receiver,
      UUID sender,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(receiver);
    this.sender = sender;
    this.pRef = pRef;
    this.pMin = pMin;
    this.pMax = pMax;
  }

  /**
   * Flex option that will be sent to SIMONA.
   *
   * @param receiver uuid of the flex options
   * @param sender uuid of the flex options
   * @param pRef current active power
   * @param pMin minimal active power
   * @param pMax maximal active power
   * @param delay the delay of the message
   */
  public FlexOptions(
      UUID receiver,
      UUID sender,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax,
      Optional<ComparableQuantity<Time>> delay) {
    super(receiver, delay);
    this.sender = sender;
    this.pRef = pRef;
    this.pMin = pMin;
    this.pMax = pMax;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FlexOptions that = (FlexOptions) o;
    return Objects.equals(sender, that.sender)
        && Objects.equals(pRef, that.pRef)
        && Objects.equals(pMin, that.pMin)
        && Objects.equals(pMax, that.pMax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), sender, pRef, pMin, pMax);
  }

  @Override
  public String toString() {
    return "FlexOptions{"
        + "receiver="
        + receiver
        + ", sender="
        + sender
        + ", pRef="
        + pRef
        + ", pMin="
        + pMin
        + ", pMax="
        + pMax
        + ", delay="
        + delay
        + '}';
  }
}
