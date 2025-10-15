package edu.ie3.simona.api.data.model.com;

import java.util.UUID;

public record CommunicationMessage<D>(UUID receiver, UUID sender, UUID msgId, D content, Class<D> dataClass) {}
