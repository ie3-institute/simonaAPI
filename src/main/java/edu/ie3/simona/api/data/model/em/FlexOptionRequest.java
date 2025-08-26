/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.UUID;

/**
 * Energy management flex option request that will be sent to SIMONA.
 *
 * @param receiver The receiver of the request.
 * @param sender The sender of the request.
 */
public record FlexOptionRequest(UUID receiver, UUID sender) implements EmData {

  @Override
  public UUID getReceiver() {
    return receiver;
  }

  @Override
  public UUID getSender() {
    return sender;
  }
}
