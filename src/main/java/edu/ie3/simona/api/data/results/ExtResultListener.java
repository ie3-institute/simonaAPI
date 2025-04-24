/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.simona.api.data.ExtOutputDataConnection;
import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * External result listener. This listener is similar to the {@link ExtResultDataConnection}, but is
 * not able to request results from SIMONA.
 */
public class ExtResultListener implements ExtOutputDataConnection<ResultDataResponseMessageToExt> {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<ResultDataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  public ExtResultListener() {}

  public final void queueExtResponseMsg(ResultDataResponseMessageToExt msg)
      throws InterruptedException {
    receiveTriggerQueue.put(msg);
  }

  public final ResultDataResponseMessageToExt receiveAny() throws InterruptedException {
    return receiveTriggerQueue.take();
  }

  @SuppressWarnings("unchecked")
  public final <T extends ResultDataResponseMessageToExt> T receiveWithType(
      Class<T> expectedMessageClass) throws InterruptedException {
    // blocks until actor puts something here
    ResultDataResponseMessageToExt msg = receiveTriggerQueue.take();

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
