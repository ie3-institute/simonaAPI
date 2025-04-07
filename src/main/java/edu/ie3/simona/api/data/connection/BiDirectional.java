/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.data.ontology.DataMessageFromExt;
import edu.ie3.simona.api.data.ontology.DataResponseMessageToExt;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Enables bidirectional communication when extended by an external data connection.
 *
 * @param <M> type of message to SIMONA
 * @param <R> type of response messages to ext
 */
public abstract non-sealed class BiDirectional<
        M extends DataMessageFromExt, R extends DataResponseMessageToExt>
    extends ExtInputDataConnection<M> implements ExtOutputDataConnection<R> {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<R> receiveTriggerQueue = new LinkedBlockingQueue<>();

  protected BiDirectional() {
    super();
  }

  @Override
  public final void queueExtResponseMsg(R msg) throws InterruptedException {
    receiveTriggerQueue.put(msg);
  }

  @Override
  public final R receiveAny() throws InterruptedException {
    return receiveTriggerQueue.take();
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <T extends R> T receiveWithType(Class<T> expectedMessageClass)
      throws InterruptedException {
    // blocks until actor puts something here
    R msg = receiveTriggerQueue.take();

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
