/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.ontology.simulation.ControlMessageToExt;
import edu.ie3.simona.api.ontology.simulation.ControlResponseMessageFromExt;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.pekko.actor.typed.ActorRef;

public final class ExtSimDataConnection implements ExtDataConnection {

  /** Queue of triggers the external simulation needs to handle. */
  private final LinkedBlockingQueue<ControlMessageToExt> receiveMessageQueue =
      new LinkedBlockingQueue<>();

  /** Actor reference to the adapter for the phases that handles scheduler control flow in SIMONA */
  private final ActorRef<ControlResponseMessageFromExt> extSimAdapter;

  // important trigger queue must be the same as held in actor
  // to make it safer one might consider asking the actor for a reference on its trigger queue?!
  public ExtSimDataConnection(ActorRef<ControlResponseMessageFromExt> extSimAdapter) {
    this.extSimAdapter = extSimAdapter;
  }

  /**
   * Called within SIMONA to queue messages for the external simulation
   *
   * @param msg the message to queue
   * @throws InterruptedException if the thread running this has been interrupted during waiting for
   *     the message to be queued
   */
  public void queueExtMsg(ControlMessageToExt msg) throws InterruptedException {
    receiveMessageQueue.put(msg);
  }

  /**
   * Method that waits for a new message to arrive. This method is blocking.
   *
   * @return the first message that arrives
   * @throws InterruptedException if the thread running this has been interrupted during waiting for
   *     the message to be queued
   */
  public ControlMessageToExt receive() throws InterruptedException {
    return receiveMessageQueue.take();
  }

  /**
   * Sends a response message to SIMONA for some message that was received by the external
   * simulation
   *
   * @param msg the message to send
   */
  public void send(ControlResponseMessageFromExt msg) {
    extSimAdapter.tell(msg);
  }
}
