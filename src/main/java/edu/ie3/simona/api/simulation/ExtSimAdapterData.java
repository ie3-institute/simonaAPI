/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import akka.actor.ActorRef;
import edu.ie3.simona.api.simulation.ontology.ExtSimMessage;
import edu.ie3.simona.api.simulation.ontology.ExtSimMessageResponse;
import java.util.concurrent.LinkedBlockingQueue;

public class ExtSimAdapterData {

  public final LinkedBlockingQueue<ExtSimMessage> receiveMessageQueue = new LinkedBlockingQueue<>();
  private final ActorRef extSimAdapter;

  private final String[] mainArgs;

  // important trigger queue must be the same as held in actor
  // to make it safer one might consider asking the actor for a reference on its trigger queue?!
  public ExtSimAdapterData(ActorRef extSimAdapter, String[] mainArgs) {
    this.extSimAdapter = extSimAdapter;
    this.mainArgs = mainArgs;
  }

  public void queueExtMsg(ExtSimMessage msg) {
    try {
      receiveMessageQueue.put(msg);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void send(ExtSimMessageResponse msg) {
    extSimAdapter.tell(msg, ActorRef.noSender());
  }

  public String[] getMainArgs() {
    return mainArgs;
  }
}
