/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Time;
import tech.units.indriya.ComparableQuantity;

/** Base class for messages used during communication. */
public abstract sealed class EmMessageBase permits EmSetPoint, FlexOptionRequest, FlexOptions {

  /** The receiver of the message. */
  public final UUID receiver;

  /** An option for the delay of this message. */
  public final Optional<ComparableQuantity<Time>> delay;

  /**
   * Base constructor without {@link #delay}.
   *
   * @param receiver of the message
   */
  protected EmMessageBase(UUID receiver) {
    this.receiver = receiver;
    this.delay = Optional.empty();
  }

  /**
   * Base constructor with {@link #delay}.
   *
   * @param receiver of this message
   * @param delay of this message
   */
  protected EmMessageBase(UUID receiver, Optional<ComparableQuantity<Time>> delay) {
    this.receiver = receiver;
    this.delay = delay;
  }

  /** Returns {@code true}, if there is a delay. */
  public boolean hasDelay() {
    return delay.isPresent();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    EmMessageBase that = (EmMessageBase) o;
    return Objects.equals(receiver, that.receiver) && Objects.equals(delay, that.delay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(receiver, delay);
  }

  @Override
  public String toString() {
    return "EmMessageBase{" + "receiver=" + receiver + ", delay=" + delay + '}';
  }
}
