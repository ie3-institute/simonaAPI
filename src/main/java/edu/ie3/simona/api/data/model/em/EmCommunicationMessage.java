/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.UUID;

/**
 * Model of communication messages that can be sent between em agents.
 *
 * @param receiver of the data
 * @param sender of the data
 * @param msgId the message id of this message
 * @param content the actual em data that is sent
 * @param <D> type of the em data
 */
public record EmCommunicationMessage<D extends EmData>(
    UUID receiver, UUID sender, UUID msgId, D content) implements EmData {

  /**
   * Constructor for {@link EmCommunicationMessage} that will generate a random message id.
   *
   * @param receiver of the data
   * @param sender of the data
   * @param content the actual em data that is sent
   */
  public EmCommunicationMessage(UUID receiver, UUID sender, D content) {
    this(receiver, sender, UUID.randomUUID(), content);
  }
}
