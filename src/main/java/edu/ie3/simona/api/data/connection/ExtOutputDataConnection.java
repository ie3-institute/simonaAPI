/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.ontology.DataResponseMessageToExt;

/**
 * Interface for a connection between SIMONA and an external simulation with data flow from SIMONA
 * to external.
 *
 * @param <T> type of response messages to ext
 */
public sealed interface ExtOutputDataConnection<T extends DataResponseMessageToExt> extends ExtDataConnection
    permits BiDirectional, ExtResultListener {

  /** Queues message from SIMONA that should be handled by the external simulation. */
  void queueExtResponseMsg(T msg) throws InterruptedException;

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
  T receiveAny() throws InterruptedException;

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
  <R extends T> R receiveWithType(Class<R> expectedMessageClass) throws InterruptedException;
}
