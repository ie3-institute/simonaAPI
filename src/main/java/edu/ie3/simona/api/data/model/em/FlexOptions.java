/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import tech.units.indriya.ComparableQuantity;

/**
 * Flex option that will be sent to SIMONA.
 *
 * @param receiver uuid of the flex options
 * @param sender uuid of the flex options
 *  @param pRef current active power
 * @param pMin minimal active power
 * @param pMax maximal active power
 * @param delay the delay of the message
 */
public record FlexOptions(
    UUID receiver,
    UUID sender,
    ComparableQuantity<Power> pRef,
    ComparableQuantity<Power> pMin,
    ComparableQuantity<Power> pMax,
    Optional<ComparableQuantity<Time>> delay) {

    public static FlexOptions of(
            UUID receiver,
            UUID sender,
            ComparableQuantity<Power> pRef,
            ComparableQuantity<Power> pMin,
            ComparableQuantity<Power> pMax
    ) {
        return new FlexOptions(receiver, sender, pRef, pMin, pMax, Optional.empty());
    }

}
