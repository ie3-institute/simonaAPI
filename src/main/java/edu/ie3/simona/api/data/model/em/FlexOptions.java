/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Flex option that will be sent to SIMONA.
 *
 * @param receiver The receiver of the flex options.
 * @param sender The sender of the flex options.
 * @param pRef Active power (might be negative, thus feed-in) that was suggested for regular usage.
 * @param pMin Minimal active power to which the sender can be reduced (might be negative, thus
 *     feed-in), that was determined by the system. Therefore, equates to lower bound of possible
 *     flexibility provision.
 * @param pMax Maximum active power to which the sender can be increased (might be negative, thus
 *     feed-in), that was determined by the system. Therefore, equates to upper bound of possible
 *     flexibility provision.
 */
public record FlexOptions(
    UUID receiver,
    UUID sender,
    ComparableQuantity<Power> pRef,
    ComparableQuantity<Power> pMin,
    ComparableQuantity<Power> pMax,
    Map<UUID, FlexOptionsResult> disaggregated)
    implements EmData {

  public FlexOptions(
      UUID receiver,
      UUID sender,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    this(receiver, sender, pRef, pMin, pMax, Collections.emptyMap());
  }

  /** Converts and returns this object as a {@link ExtendedFlexOptionsResult}. */
  public ExtendedFlexOptionsResult asResult() {
    return new ExtendedFlexOptionsResult(null, sender, receiver, pRef, pMin, pMax, disaggregated);
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
