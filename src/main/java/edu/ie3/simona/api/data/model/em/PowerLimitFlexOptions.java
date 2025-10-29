/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Power limit flex option that will be sent to SIMONA.
 *
 * @param receiver The uuid of the receiver. It can be the same as the model.
 * @param model That is providing this flex options.
 * @param pRef Active power (might be negative, thus feed-in) that was suggested for regular usage.
 * @param pMin Minimal active power to which the sender can be reduced (might be negative, thus
 *     feed-in), that was determined by the system. Therefore, equates to lower bound of possible
 *     flexibility provision.
 * @param pMax Maximum active power to which the sender can be increased (might be negative, thus
 *     feed-in), that was determined by the system. Therefore, equates to upper bound of possible
 *     flexibility provision.
 * @param disaggregated A map: uuid to disaggregated flex options.
 */
public record PowerLimitFlexOptions(
    UUID receiver,
    UUID model,
    ComparableQuantity<Power> pRef,
    ComparableQuantity<Power> pMin,
    ComparableQuantity<Power> pMax,
    Map<UUID, FlexOptions> disaggregated)
    implements FlexOptions {

  public PowerLimitFlexOptions(
      UUID receiver,
      UUID model,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    this(receiver, model, pRef, pMin, pMax, new HashMap<>());
  }

  @Override
  public void addDisaggregated(UUID model, FlexOptions flexOptions) {
    disaggregated.put(model, flexOptions);
  }
}
