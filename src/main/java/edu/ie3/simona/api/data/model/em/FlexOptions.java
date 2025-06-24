/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Flex option that will be sent to SIMONA.
 *
 * @param receiver uuid of the flex options
 * @param sender uuid of the flex options
 * @param pMin minimal active power
 * @param pRef current active power
 * @param pMax maximal active power
 */
public record FlexOptions(
    UUID receiver,
    UUID sender,
    ComparableQuantity<Power> pMin,
    ComparableQuantity<Power> pRef,
    ComparableQuantity<Power> pMax) {}
