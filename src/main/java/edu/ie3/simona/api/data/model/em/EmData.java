/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.UUID;

/** Interface that is extended by all em data models. */
public interface EmData {

  /** Returns the uuid of the receiver. */
  UUID getReceiver();

  /** Returns the uuid of the sender. */
  UUID getSender();
}
