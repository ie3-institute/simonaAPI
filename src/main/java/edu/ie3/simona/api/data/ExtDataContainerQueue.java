/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.simona.api.data.container.ExtDataContainer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;

/** Data queue to allow data flow between SimonaAPI and an external simulation */
public final class ExtDataContainerQueue<V extends ExtDataContainer> {
  private final LinkedBlockingDeque<V> receiverTriggerDeque = new LinkedBlockingDeque<>();

  /**
   * Method for adding an {@link ExtDataContainer} to the queue.
   *
   * @param data to be added
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public void queueData(V data) throws InterruptedException {
    receiverTriggerDeque.putLast(data);
  }

  /**
   * Method to take an {@link ExtDataContainer} from the queue. This method waits (blocks) until
   * data is added to the queue.
   *
   * @return a data container
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public V takeContainer() throws InterruptedException {
    return receiverTriggerDeque.takeFirst();
  }

  /**
   * Method to take only a part of a container from the queue. This method waits (blocks) until data
   * is added to the queue.
   *
   * @param extractor function to extract a part of the container
   * @return the extracted part
   * @param <R> type of returned value
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public <R> R takeData(Function<V, R> extractor) throws InterruptedException {
    // removes the first container from the queue
    V data = receiverTriggerDeque.takeFirst();
    R result = extractor.apply(data);

    // if the container is not empty, it should remain in the queue.
    // else the container needs to be removed
    if (!data.isEmpty()) {
      receiverTriggerDeque.putFirst(data);
    }

    return result;
  }
}
