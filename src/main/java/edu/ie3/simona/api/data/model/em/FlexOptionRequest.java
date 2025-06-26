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

/** Energy management flex option request that will be sent to SIMONA. */
public final class FlexOptionRequest extends EmMessageBase {

  /** The sender of the request. */
  public final Optional<UUID> sender;

  /**
   * Constructor for {@link FlexOptionRequest}. Equals {@code new FlexOptionRequest(receiver,
   * Optional.empty())}.
   *
   * @param receiver of the request
   */
  public FlexOptionRequest(UUID receiver) {
    this(receiver, Optional.empty());
  }

  /**
   * Constructor for {@link FlexOptionRequest}.
   *
   * @param receiver of the request
   * @param sender of the request
   */
  public FlexOptionRequest(UUID receiver, UUID sender) {
    super(receiver);
    this.sender = Optional.ofNullable(sender);
  }

  /**
   * Constructor for {@link FlexOptionRequest}.
   *
   * @param receiver of the request
   * @param sender of the request
   */
  public FlexOptionRequest(UUID receiver, Optional<UUID> sender) {
    super(receiver);
    this.sender = sender;
  }

  /**
   * Constructor for {@link FlexOptionRequest}.
   *
   * @param receiver of the request
   * @param sender of the request
   * @param delay option for the delay of this message
   */
  public FlexOptionRequest(UUID receiver, UUID sender, Optional<ComparableQuantity<Time>> delay) {
    super(receiver, delay);
    this.sender = Optional.ofNullable(sender);
  }

  /**
   * Constructor for {@link FlexOptionRequest}.
   *
   * @param receiver of the request
   * @param sender option for the sender of the request
   * @param delay option for the delay of this message
   */
  public FlexOptionRequest(
      UUID receiver, Optional<UUID> sender, Optional<ComparableQuantity<Time>> delay) {
    super(receiver, delay);
    this.sender = sender;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FlexOptionRequest that = (FlexOptionRequest) o;
    return Objects.equals(sender, that.sender);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delay);
  }

  @Override
  public String toString() {
    return "FlexOptionRequest{"
        + "receiver="
        + receiver
        + ", sender="
        + sender
        + ", delay="
        + delay
        + '}';
  }
}
