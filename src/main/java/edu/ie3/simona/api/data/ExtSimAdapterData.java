/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.simona.api.ontology.simulation.ControlMessageToExt;
import edu.ie3.simona.api.ontology.simulation.ControlResponseMessageFromExt;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.pekko.actor.typed.ActorRef;

public class ExtSimAdapterData {

  /** Queue of triggers the external simulation needs to handle. */
  public final LinkedBlockingQueue<ControlMessageToExt> receiveMessageQueue =
      new LinkedBlockingQueue<>();

  /** Actor reference to the adapter for the phases that handles scheduler control flow in SIMONA */
  private final ActorRef<ControlResponseMessageFromExt> extSimAdapter;

  /** CLI arguments with which SIMONA is initiated */
  private final String[] mainArgs;

  // important trigger queue must be the same as held in actor
  // to make it safer one might consider asking the actor for a reference on its trigger queue?!
  public ExtSimAdapterData(
      ActorRef<ControlResponseMessageFromExt> extSimAdapter, String[] mainArgs) {
    this.extSimAdapter = extSimAdapter;
    this.mainArgs = mainArgs;
  }

  public ActorRef<ControlResponseMessageFromExt> getAdapter() {
    return extSimAdapter;
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
   * Sends a response message to SIMONA for some message that was received by the external
   * simulation
   *
   * @param msg the message to send
   */
  public void send(ControlResponseMessageFromExt msg) {
    extSimAdapter.tell(msg);
  }

  public String[] getMainArgs() {
    return mainArgs;
  }
}
