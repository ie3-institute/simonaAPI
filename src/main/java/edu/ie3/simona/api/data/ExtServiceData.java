package edu.ie3.simona.api.data;

import org.apache.pekko.actor.ActorRef;

public class ExtServiceData implements ExtData {

    /** Actor reference to service that handles ev data within SIMONA */
    private final ActorRef dataService;

    /** Actor reference to adapter that handles scheduler control flow in SIMONA */
    private final ActorRef extSimAdapter;

    // important trigger queue must be the same as hold in actor
    // to make it safer one might consider asking the actor for ara reference on its trigger queue?!
    public ExtServiceData(ActorRef dataService, ActorRef extSimAdapter) {
        this.dataService = dataService;
        this.extSimAdapter = extSimAdapter;
    }
}
