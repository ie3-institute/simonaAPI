/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.exceptions.ExtDataConnectionException;
import edu.ie3.simona.api.ontology.results.ResultDataResponseMessageToExt;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * External result listener. This listener is similar to the {@link ExtResultDataConnection}, but is
 * not able to request results from SIMONA.
 */
public abstract non-sealed class ExtResultListener
    implements ExtOutputDataConnection<ResultDataResponseMessageToExt> {

  private static final Logger log = LoggerFactory.getLogger(ExtResultListener.class);

  /** Data message queue containing messages from SIMONA */
  private final LinkedBlockingQueue<ResultDataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  private boolean stopFlag = false;

  private final Thread thread;

  protected ExtResultListener() {
    this.thread = new Thread(this::run);
    this.thread.start();
  }

  /** Method that is run in the thread. */
  private void run() {
    boolean finished = receiveTriggerQueue.isEmpty() && stopFlag;

    while (!finished) {
      try {
        processResponse(receiveTriggerQueue.take());
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();

        if (!stopFlag) {
          // to prevent exception after successful termination
          throw new ExtDataConnectionException(
              "An exception occurred while processing the result.", ie);
        }
      }

      finished = receiveTriggerQueue.isEmpty() && stopFlag;
    }
  }

  @Override
  public void handleResponseMsg(ResultDataResponseMessageToExt msg) throws InterruptedException {
    if (!stopFlag) {
      receiveTriggerQueue.put(msg);
    } else {
      log.warn(
          "Cannot process result message, because the listener is already terminated. Msg: {}",
          msg);
    }
  }

  /** Stops the current listener. */
  public final void stop() {
    stopFlag = true;
    try {
      close();
    } catch (Throwable t) {
      log.error("An error occurred while closing the listener.", t);
    } finally {
      thread.interrupt();
    }
  }

  /**
   * Method to handle the message.
   *
   * @param msg To handle.
   */
  public abstract void processResponse(ResultDataResponseMessageToExt msg);

  /** Method to implement some clean up operations. */
  public abstract void close();
}
