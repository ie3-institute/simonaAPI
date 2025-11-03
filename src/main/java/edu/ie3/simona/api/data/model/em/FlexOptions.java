/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.Map;
import java.util.UUID;

/** Interface that defines flex options. */
public interface FlexOptions extends EmData {

  /** Returns the receiver of this flex options. */
  UUID receiver();

  /** Returns the disaggregated flex options. */
  Map<UUID, FlexOptions> disaggregated();

  /**
   * Enriches the flex option with disaggregated flex options.
   *
   * @param model of the flex options
   * @param flexOptions to include
   */
  void addDisaggregated(UUID model, FlexOptions flexOptions);
}
