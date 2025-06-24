/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt;
import edu.ie3.simona.api.exceptions.UnexpectedResponseMessageException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * External result listener. This listener is similar to the {@link ExtResultDataConnection}, but is
 * not able to request results from SIMONA.
 */
public non-sealed class ExtResultListener
    implements ExtOutputDataConnection<ResultDataResponseMessageToExt> {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<ResultDataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  public ExtResultListener() {
    super();
  }

  @Override
  public final void queueExtResponseMsg(ResultDataResponseMessageToExt msg)
      throws InterruptedException {
    receiveTriggerQueue.put(msg);
  }

  @Override
  public final ResultDataResponseMessageToExt receiveAny() throws InterruptedException {
    return receiveTriggerQueue.take();
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <T extends ResultDataResponseMessageToExt> T receiveWithType(
      Class<T> expectedMessageClass) throws InterruptedException {
    // blocks until actor puts something here
    ResultDataResponseMessageToExt msg = receiveTriggerQueue.take();

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
