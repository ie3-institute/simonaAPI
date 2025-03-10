/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.simona.api.data.ontology.DataResponseMessageToExt;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Enables receiving data responses form SIMONA.
 *
 * @param <T> type of response messages to ext
 */
public abstract class WithDataResponseToExt<T extends DataResponseMessageToExt> {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<T> receiveTriggerQueue = new LinkedBlockingQueue<>();

  /** Queues message from SIMONA that should be handled by the external simulation. */
  public void queueExtResponseMsg(T msg) throws InterruptedException {
    receiveTriggerQueue.put(msg);
  }

  /**
   * Waits until a message of given type is added to the queue. If the message has a different type,
   * a RuntimeException is thrown. This method blocks until having received a response from SIMONA.
   *
   * @param expectedMessageClass the expected class of the message to be received
   * @return a message of the expected type once it has been received
   * @param <R> the type of the expected message
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  @SuppressWarnings("unchecked")
  protected <R extends T> R receiveWithType(Class<R> expectedMessageClass)
      throws InterruptedException {
    // blocks until actor puts something here
    T msg = receiveTriggerQueue.take();

    if (msg.getClass().equals(expectedMessageClass)) {
      return (R) msg;
    } else
      throw new RuntimeException(
          "Received unexpected message '"
              + msg
              + "', expected type '"
              + expectedMessageClass
              + "'");
  }
}
