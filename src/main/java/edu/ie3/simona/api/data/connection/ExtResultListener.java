/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.ontology.results.ResultDataResponseMessageToExt;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * External result listener. This listener is similar to the {@link ExtResultDataConnection}, but is
 * not able to request results from SIMONA.
 */
public abstract non-sealed class ExtResultListener implements ExtOutputDataConnection<ResultDataResponseMessageToExt> {

  /** Data message queue containing messages from SIMONA */
  private final LinkedBlockingQueue<ResultDataResponseMessageToExt> receiveTriggerQueue = new LinkedBlockingQueue<>();

  private final Thread thread;

  public ExtResultListener() {
    Runnable run = () -> {
      while (!Thread.currentThread().isInterrupted()) {
          try {
            ResultDataResponseMessageToExt msg = receiveTriggerQueue.take();
              processResponse(msg);
          } catch (Exception ignored) {}
      }
    };

    this.thread = new Thread(run);
    this.thread.start();
  }


  @Override
  public void handleResponseMsg(ResultDataResponseMessageToExt msg) throws InterruptedException {
    receiveTriggerQueue.put(msg);
  }

  /**
   * Stops the current listener.
   */
  public final void stop() {
    thread.interrupt();
    close();
  }

  /**
   * Method to handle the message.
   * @param msg To handle.
   */
  public abstract void processResponse(ResultDataResponseMessageToExt msg);

  /**
   * Method to implement some clean up operations.
   */
  public abstract void close();

}
