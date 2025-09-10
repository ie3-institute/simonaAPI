/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.value.PValue;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Energy management set point that will be sent to SIMONA.
 *
 * @param receiver The receiver of the set point.
 * @param sender The sender of the set point.
 * @param power An option for the em set point.
 */
public record EmSetPoint(UUID receiver, UUID sender, Optional<PValue> power) implements EmData {
  /**
   * Constructor for {@link EmSetPoint}.
   *
   * <p>Note: Using this constructor will signal SIMONA, that the current set point should be kept.
   *
   * @param receiver The receiver of the set point.
   * @param sender The sender of the set point.
   */
  public EmSetPoint(UUID receiver, UUID sender) {
    this(receiver, sender, Optional.empty());
  }

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * @param receiver The receiver of the set point.
   * @param sender The sender of the set point.
   * @param p Power value of the set point.
   */
  public EmSetPoint(UUID receiver, UUID sender, ComparableQuantity<Power> p) {
    this(receiver, sender, Optional.of(new PValue(p)));
  }

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * @param receiver The receiver of the set point.
   * @param sender The sender of the set point.
   * @param power value of the set point.
   */
  public EmSetPoint(UUID receiver, UUID sender, PValue power) {
    this(receiver, sender, Optional.ofNullable(power));
  }

  @Override
  public UUID getReceiver() {
    return receiver;
  }

  @Override
  public UUID getSender() {
    return sender;
  }
}
