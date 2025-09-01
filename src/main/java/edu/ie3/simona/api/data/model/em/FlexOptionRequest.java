/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.Optional;
import java.util.UUID;

/**
 * Energy management flex option request that will be sent to SIMONA
 *
 * @param receiver The receiver of the message.
 * @param sender The sender of the request.
 * @param disaggregated True, if disaggregated flex options should be returned.
 */
public record FlexOptionRequest(UUID receiver, Optional<UUID> sender, boolean disaggregated) {
  /**
   * Constructor for {@link FlexOptionRequest}. Equals {@code new FlexOptionRequest(receiver,
   * Optional.empty())}.
   *
   * @param receiver of the request
   */
  public FlexOptionRequest(UUID receiver) {
    this(receiver, Optional.empty(), false);
  }

  /**
   * Constructor for {@link FlexOptionRequest}.
   *
   * @param receiver of the request
   * @param sender of the request
   */
  public FlexOptionRequest(UUID receiver, UUID sender) {
    this(receiver, Optional.ofNullable(sender), false);
  }
}
