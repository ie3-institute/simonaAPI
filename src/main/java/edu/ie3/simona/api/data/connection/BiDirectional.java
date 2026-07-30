/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.exceptions.UnexpectedResponseMessageException;
import edu.ie3.simona.api.ontology.DataMessageFromExt;
import edu.ie3.simona.api.ontology.DataResponseMessageToExt;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Enables bidirectional communication when extended by an external data connection.
 *
 * @param <M> type of message to SIMONA
 * @param <R> type of response messages to ext
 */
public abstract sealed class BiDirectional<
        M extends DataMessageFromExt, R extends DataResponseMessageToExt>
    extends ExtInputDataConnection<M> implements ExtOutputDataConnection<R>
    permits ExtEmDataConnection, ExtEvDataConnection, ExtResultDataConnection {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<R> receiveTriggerQueue = new LinkedBlockingQueue<>();

  protected BiDirectional() {
    super();
  }


  @Override
  public final void handleResponseMsg(R msg) throws InterruptedException {
    receiveTriggerQueue.put(msg);
  }

  /**
   * Waits until a message of given type is added to the queue. All messages that extends the given
   * type can be received. This method blocks until having received a response from SIMONA.
   *
   * <p>To receive only specific types of messages, use {@link #receiveWithType(Class)} instead.
   *
   * @return a message of the given type
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public final R receiveAny() throws InterruptedException {
    return receiveTriggerQueue.take();
  }

  /** Returns all received responses. */
  public final List<R> receiveAll() {
    List<R> result = new ArrayList<>(receiveTriggerQueue.size());
    receiveTriggerQueue.drainTo(result);
    return result;
  }

  /**
   * Waits until a message of given type is added to the queue. If the message has a different type,
   * a RuntimeException is thrown. This method blocks until having received a response from SIMONA.
   *
   * @param expectedMessageClass the expected class of the message to be received
   * @return a message of the expected type once it has been received
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  @SuppressWarnings("unchecked")
  public final <T extends R> T receiveWithType(Class<T> expectedMessageClass)
      throws InterruptedException {
    // blocks until actor puts something here
    R msg = receiveTriggerQueue.take();

    if (msg.getClass().equals(expectedMessageClass)) {
      return (T) msg;
    } else
      throw new UnexpectedResponseMessageException(
          "Received unexpected message '"
              + msg
              + "', expected type '"
              + expectedMessageClass
              + "'");
  }
}
