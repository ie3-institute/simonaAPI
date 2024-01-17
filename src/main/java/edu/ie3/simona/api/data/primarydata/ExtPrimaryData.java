package edu.ie3.simona.api.data.primarydata;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.primarydata.ontology.PrimaryDataMessageFromExt;
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData;
import edu.ie3.simona.api.data.results.ontology.ResultDataMessageToExt;
import org.apache.pekko.actor.ActorRef;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class ExtPrimaryData implements ExtData {

    public final LinkedBlockingQueue<PrimaryDataMessageFromExt> inputsFromExtQueue =
            new LinkedBlockingQueue<>();

    public final LinkedBlockingQueue<ResultDataMessageToExt> resultToExtQueue =
            new LinkedBlockingQueue<>();

    /** Actor reference to service that handles ev data within SIMONA */
    private final ActorRef dataService;

    /** Actor reference to adapter that handles scheduler control flow in SIMONA */
    private final ActorRef extSimAdapter;

    public ExtPrimaryData(ActorRef dataService, ActorRef extSimAdapter) {
        this.dataService = dataService;
        this.extSimAdapter = extSimAdapter;
    }

    public void putPrimaryDataInQueue(long tick, Map<UUID, Value> primaryData) throws InterruptedException {
        inputsFromExtQueue.put(new ProvidePrimaryData(tick, primaryData));
    }

    public void providePrimaryData(long tick, Map<UUID, Value> primaryData) {
        sendExtMsg(new ProvidePrimaryData(tick, primaryData));
    }

    public void requestSimulatedAssets(

    ) {

    }

    public void sendExtMsg(PrimaryDataMessageFromExt msg) {
        dataService.tell(msg, ActorRef.noSender());
        // we need to schedule data receiver activation with scheduler
        extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
    }

    public long receiveFinishMessageFromSimona() throws InterruptedException {
        // blocks until actor puts something here
        ResultDataMessageToExt resultMessage = resultToExtQueue.take();
        return 0;
    }


}
