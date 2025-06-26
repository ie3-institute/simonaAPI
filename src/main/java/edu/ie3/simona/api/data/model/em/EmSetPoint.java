/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.value.PValue;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import tech.units.indriya.ComparableQuantity;

/** Energy management set point that will be sent to SIMONA. */
public final class EmSetPoint extends EmMessageBase {

  /** An option for the em set point. */
  public final Optional<PValue> power;
  public final Optional<ComparableQuantity<Time>> delay;

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * <p>Note: Using this constructor will signal SIMONA, that the current set point should be kept.
   *
   * @param receiver of the set point.
   */
  public EmSetPoint(UUID receiver) {
    super(receiver);
    this.power = Optional.empty();
    this.delay = Optional.empty();
  }

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * @param receiver of the set point.
   * @param p power value of the set point
   */
  public EmSetPoint(UUID receiver, ComparableQuantity<Power> p) {
    super(receiver);
    this.power = Optional.of(new PValue(p));
    this.delay = Optional.empty();
  }

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * @param receiver of the set point.
   * @param power value of the set point
   */
  public EmSetPoint(UUID receiver, PValue power) {
    super(receiver);
    this.power = Optional.of(power);
    this.delay = Optional.empty();
  }

  public EmSetPoint(
      UUID receiver, Optional<PValue> power, Optional<ComparableQuantity<Time>> delay) {
    this.receiver = receiver;
    this.power = power;
    this.delay = delay;
  }

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * @param receiver of the set point.
   * @param power option for the set point
   * @param delay option for the delay of this message
   */
  public EmSetPoint(
      UUID receiver, Optional<PValue> power, Optional<ComparableQuantity<Time>> delay) {
    super(receiver, delay);
    this.power = power;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    EmSetPoint that = (EmSetPoint) o;
    return Objects.equals(power, that.power);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), power);
  }

  @Override
  public String toString() {
    return "EmSetPoint{" + "receiver=" + receiver + ", power=" + power + ", delay=" + delay + '}';
  }
}
