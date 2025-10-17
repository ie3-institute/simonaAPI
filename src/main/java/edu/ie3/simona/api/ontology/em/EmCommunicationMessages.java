package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.EmCommunicationMessage;
import edu.ie3.simona.api.data.model.em.EmData;

import java.util.Collection;
import java.util.Optional;

public record EmCommunicationMessages(long tick, Collection<EmCommunicationMessage<? extends EmData>> messages, Optional<Long> maybeNextTick) implements EmDataMessageFromExt, EmDataResponseMessageToExt {}
