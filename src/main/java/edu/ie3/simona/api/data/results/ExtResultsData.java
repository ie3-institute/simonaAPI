package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ev.ontology.EvDataResponseMessageToExt;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities;
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities;
import edu.ie3.simona.api.data.results.ontology.ResultDataMessageFromExt;
import edu.ie3.simona.api.data.results.ontology.ResultDataMessageToExt;
import org.apache.pekko.actor.ActorRef;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class ExtResultsData implements ExtData {

    /** Data message queue containing messages from SIMONA */
    public final LinkedBlockingQueue<ResultDataMessageToExt> receiveTriggerQueue =
            new LinkedBlockingQueue<>();


    /** Actor reference to service that handles ev data within SIMONA */
    private final ActorRef dataService;

    /** Actor reference to adapter that handles scheduler control flow in SIMONA */
    private final ActorRef extSimAdapter;

    // important trigger queue must be the same as hold in actor
    // to make it safer one might consider asking the actor for ara reference on its trigger queue?!
    public ExtResultsData(ActorRef dataService, ActorRef extSimAdapter) {
        this.dataService = dataService;
        this.extSimAdapter = extSimAdapter;
    }

    public List<ResultEntity> requestResults() throws InterruptedException {
        sendExtMsg(new RequestResultEntities());
        return receiveWithType(ProvideResultEntities.class).resultEntityMap();
    }

    public void sendExtMsg(ResultDataMessageFromExt msg) {
        dataService.tell(msg, ActorRef.noSender());
        // we need to schedule data receiver activation with scheduler
        extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
    }

    public void queueExtResponseMsg(ResultDataMessageToExt msg)
            throws InterruptedException {
        receiveTriggerQueue.put(msg);
    }

    private <T extends ResultDataMessageToExt> T receiveWithType(Class<T> expectedMessageClass)
            throws InterruptedException {

        // blocks until actor puts something here
        ResultDataMessageToExt msg = receiveTriggerQueue.take();

        if (msg.getClass().equals(expectedMessageClass)) {
            return (T) msg;
        } else
            throw new RuntimeException(
                    "Received unexpected message '"
                            + msg
                            + "', expected type '"
                            + expectedMessageClass
                            + "'");
    }







}
