
package edu.ie3.simona.api.data.dcopf;

import akka.actor.ActorRef;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.dcopf.ontology.ExtOpfMessage;
import edu.ie3.simona.api.data.dcopf.ontology.ExtOpfResponseMessage;
import edu.ie3.simona.api.data.dcopf.ontology.SetpointsMessage;
import edu.ie3.simona.api.data.ev.ontology.AllDepartedEvsResponse;
import edu.ie3.simona.api.data.ev.ontology.ExtEvMessage;
import edu.ie3.simona.api.data.ev.ontology.ExtEvResponseMessage;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class ExtOpfData implements ExtData {
    public final LinkedBlockingQueue<ExtOpfResponseMessage> receiveTriggerQueue =
            new LinkedBlockingQueue<>();    //brauche ich das hier?
    private final ActorRef dataService;
    private final ActorRef extSimAdapter;

    public ExtOpfData(ActorRef dataService, ActorRef extSimAdapter) {
        this.dataService = dataService;
        this.extSimAdapter = extSimAdapter;
    }

    public List<PValue> sendSetpoints(SetpointsMessage setpointsMessage) {
        sendExtMsg(setpointsMessage);
        return new ArrayList<>(); //warum ist die hier leer?
    }

    public void sendExtMsg(ExtOpfMessage msg) {
        dataService.tell(msg, ActorRef.noSender());     // hier ist der dataService der OPFService
        // we need to schedule data receiver activation with scheduler
        extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender()); //damit Activation Message vom Scheduler getriggert wird
    } // nochmal im Sequenzdiagramm nachschauen.... es gehen zwei nachrichten raus

}