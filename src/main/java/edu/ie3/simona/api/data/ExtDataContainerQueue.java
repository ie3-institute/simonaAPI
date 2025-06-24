/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.simona.api.data.container.ExtDataContainer;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/** Data queue to allow data flow between SimonaAPI and an external simulation */
public final class ExtDataContainerQueue<V extends ExtDataContainer> {
  private final LinkedBlockingDeque<V> receiverTriggerDeque = new LinkedBlockingDeque<>();

  /** Returns the number of elements in this queue. */
  public int size() {
    return receiverTriggerDeque.size();
  }

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
   * Method to retrieve and remove an {@link ExtDataContainer} from the queue. This method waits
   * (blocks) until data is added to the queue.
   *
   * @return a data container
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public V takeContainer() throws InterruptedException {
    return receiverTriggerDeque.takeFirst();
  }

  /**
   * Method to retrieve and remove an {@link ExtDataContainer} from the queue. This method waits
   * (blocks) until either data is added to the queue or the specified wait time is reached.
   *
   * @param timeout maximal time to wait for data
   * @param unit unit of the timeout
   * @return an option for a data container
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public Optional<V> pollContainer(long timeout, TimeUnit unit) throws InterruptedException {
    return Optional.ofNullable(receiverTriggerDeque.pollFirst(timeout, unit));
  }

  /**
   * Method to retrieve only a part of a container from the queue. This method waits (blocks) until
   * data is added to the queue.
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

  /**
   * Method to retrieve only a part of a container from the queue. This method waits (blocks) until
   * either data is added to the queue or the specified wait time is reached.
   *
   * @param extractor function to extract a part of the container
   * @param timeout maximal time to wait for data
   * @param unit unit of the timeout
   * @return an option for the extracted part
   * @param <R> type of returned value
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public <R> Optional<R> pollData(Function<V, R> extractor, long timeout, TimeUnit unit)
      throws InterruptedException {
    // removes the first container from the queue
    Optional<V> containerOption = pollContainer(timeout, unit);

    if (containerOption.isPresent()) {
      V data = containerOption.get();
      R result = extractor.apply(data);

      // if the container is not empty, it should remain in the queue.
      // else the container needs to be removed
      if (!data.isEmpty()) {
        receiverTriggerDeque.putFirst(data);
      }

      return Optional.of(result);
    }

    return Optional.empty();
  }
}
