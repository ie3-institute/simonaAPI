/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.EmCommunicationMessage;
import edu.ie3.simona.api.data.model.em.EmData;
import java.util.Collection;

/**
 * This message contains {@link EmCommunicationMessage}s that can be sent either to SIMONA or to the
 * external simulation.
 *
 * @param tick of the message
 * @param messages the communication messages that should be sent
 */
public record EmCommunicationMessages(
    long tick, Collection<EmCommunicationMessage<? extends EmData>> messages)
    implements EmDataMessageFromExt, EmDataResponseMessageToExt {}
