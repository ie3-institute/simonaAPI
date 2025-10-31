/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Container class for grouping multiple flex options to a single receiver.
 *
 * @param receiver that should receive the flex options
 * @param flexOptions that should be received
 */
public record MultiFlexOptions(UUID receiver, List<FlexOptions> flexOptions) implements EmData {
  public MultiFlexOptions(UUID receiver) {
    this(receiver, new ArrayList<>());
  }
}
