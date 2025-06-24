/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.value.PValue;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Energy management set point that will be sent to SIMONA. */
public final class EmSetPoint {
  public final UUID receiver;
  public final Optional<PValue> power;
  public final Optional<ComparableQuantity<Time>> delay;

  public EmSetPoint(UUID receiver) {
    this.receiver = receiver;
    this.power = Optional.empty();
    this.delay = Optional.empty();
  }

  public EmSetPoint(UUID receiver, ComparableQuantity<Power> p) {
    this.receiver = receiver;
    this.power = Optional.of(new PValue(p));
    this.delay = Optional.empty();
  }

  public EmSetPoint(UUID receiver, PValue power) {
    this.receiver = receiver;
    this.power = Optional.of(power);
    this.delay = Optional.empty();
  }

  public EmSetPoint(
      UUID receiver, Optional<PValue> power, Optional<ComparableQuantity<Time>> delay) {
    this.receiver = receiver;
    this.power = power;
    this.delay = delay;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    EmSetPoint that = (EmSetPoint) o;
    return Objects.equals(receiver, that.receiver) && Objects.equals(power, that.power);
  }

  @Override
  public int hashCode() {
    return Objects.hash(receiver, power);
  }

  @Override
  public String toString() {
    return "EmSetPoint{" + "receiver=" + receiver + ", power=" + power + '}';
  }
}
