import akka.actor.ActorRef;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.dcopf.ontology.ExtOpfResponseMessage;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class ExtOpfData implements ExtData {
    public final LinkedBlockingQueue<ExtOpfResponseMessage> receiveTriggerQueue =
            new LinkedBlockingQueue<>();
    private final ActorRef dataService;
    private final ActorRef extSimAdapter;

    public ExtOpfData(ActorRef dataService, ActorRef extSimAdapter) {
        this.dataService = dataService;
        this.extSimAdapter = extSimAdapter;
    }

}