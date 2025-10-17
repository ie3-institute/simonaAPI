package edu.ie3.simona.api.data.model.em;

import java.util.UUID;

public record EmCommunicationMessage<D extends EmData>(UUID receiver, UUID sender, UUID msgId, D content) implements EmData {
    public EmCommunicationMessage(UUID receiver, UUID sender, D content) {
        this(receiver, sender, UUID.randomUUID(), content);
    }
}
