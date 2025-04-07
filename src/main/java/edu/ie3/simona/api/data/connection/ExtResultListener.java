/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * External result listener. This listener is similar to the {@link ExtResultDataConnection}, but is
 * not able to request results from SIMONA.
 */
public non-sealed class ExtResultListener<R extends ResultDataResponseMessageToExt>
    implements ExtOutputDataConnection<R> {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<R> receiveTriggerQueue = new LinkedBlockingQueue<>();

  protected ExtResultListener() {
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
