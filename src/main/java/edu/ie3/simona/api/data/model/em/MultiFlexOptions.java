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
public record MultiFlexOptions(UUID receiver, Map<UUID, FlexOptions> disaggregated)
    implements FlexOptions {
  public MultiFlexOptions(UUID receiver) {
    this(receiver, new HashMap<>());
  }

  @Override
  public void addDisaggregated(UUID model, FlexOptions flexOptions) {
    disaggregated.put(model, flexOptions);
  }
}
