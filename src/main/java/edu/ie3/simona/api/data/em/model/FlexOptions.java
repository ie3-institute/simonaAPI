/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.model;

import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

import java.util.UUID;

public record FlexOptions(
        UUID sender,
    ComparableQuantity<Power> pMin,
    ComparableQuantity<Power> pRef,
    ComparableQuantity<Power> pMax) {}
