/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.model;

import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Time;
import java.util.Optional;
import java.util.UUID;

public record FlexOptionRequest(UUID receiver, Optional<UUID> sender, Optional<ComparableQuantity<Time>> delay) {}
