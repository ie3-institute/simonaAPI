package edu.ie3.simona.api.ontology.com;

import edu.ie3.simona.api.data.model.com.CommunicationMessage;
import edu.ie3.simona.api.ontology.DataMessageFromExt;
import edu.ie3.simona.api.ontology.DataResponseMessageToExt;

import java.util.Collection;
import java.util.Optional;

public record CommunicationMessages(long tick, Collection<CommunicationMessage<?>> messages, Optional<Long> maybeNextTick) implements DataMessageFromExt, DataResponseMessageToExt {}
