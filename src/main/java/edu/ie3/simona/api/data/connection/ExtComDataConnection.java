package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.data.model.com.CommunicationMessage;
import edu.ie3.simona.api.ontology.com.CommunicationMessages;

import java.util.*;

public class ExtComDataConnection extends BiDirectional<CommunicationMessages, CommunicationMessages> {
    private final Set<UUID> awaitedMessages = new HashSet<>();


    public ExtComDataConnection() {}

    /**
     * Method for sending communication messages to SIMONA.
      * @param tick current tick
     * @param messages to sent to SIMONA
     * @param maybeNextTick option for the next tick in the simulation
     * @return true, if data was sent
     */
    public boolean sendMessages(long tick, Collection<CommunicationMessage<?>> messages, Optional<Long> maybeNextTick) {
        if (!messages.isEmpty()) {
            // saving the message ids of all
            messages.forEach(message -> awaitedMessages.remove(message.msgId()));
            sendExtMsg(new CommunicationMessages(tick, messages, maybeNextTick));
            return true;
        }
        return false;
    }

    /**
     * Method to retrieve messages that should be sent to the external simulation.
     * @return a list of messages
     * @throws InterruptedException - on interruptions
     */
    public List<CommunicationMessage<?>> retrieveMessages() throws InterruptedException {
        if (awaitedMessages.isEmpty()) {
            return Collections.emptyList();
        }

        List<CommunicationMessage<?>> messages = new ArrayList<>();

        for (CommunicationMessages receivedMessage : receiveAll()) {
            messages.addAll(receivedMessage.messages());
        }

        // save all message ids
        messages.forEach(message -> awaitedMessages.add(message.msgId()));

        return messages;
    }
}
