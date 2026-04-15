/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.*;

/**
 * Container class for grouping disaggregated flex options to a single receiver.
 *
 * @param receiver that should receive the flex options
 * @param disaggregated flex options
 */
public record DisaggregatedFlexOptions<F extends FlexOptions>(
    UUID receiver, Map<UUID, F> disaggregated) implements FlexOptions {
  public DisaggregatedFlexOptions(UUID receiver) {
    this(receiver, new HashMap<>());
  }
}
