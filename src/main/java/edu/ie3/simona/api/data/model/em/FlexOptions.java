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
public final class FlexOptions {

  public final UUID receiver;
  public final UUID sender;
  public final ComparableQuantity<Power> pRef;
  public final ComparableQuantity<Power> pMin;
  public final ComparableQuantity<Power> pMax;
  public final Optional<ComparableQuantity<Time>> delay;

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
    this.receiver = receiver;
    this.sender = sender;
    this.pRef = pRef;
    this.pMin = pMin;
    this.pMax = pMax;
    this.delay = Optional.empty();
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
    this.receiver = receiver;
    this.sender = sender;
    this.pRef = pRef;
    this.pMin = pMin;
    this.pMax = pMax;
    this.delay = delay;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    FlexOptions that = (FlexOptions) o;
    return Objects.equals(receiver, that.receiver)
        && Objects.equals(sender, that.sender)
        && Objects.equals(pRef, that.pRef)
        && Objects.equals(pMin, that.pMin)
        && Objects.equals(pMax, that.pMax)
        && Objects.equals(delay, that.delay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(receiver, sender, pRef, pMin, pMax, delay);
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
