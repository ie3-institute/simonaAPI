/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.simona.api.data.datacontainer.ExtDataContainer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;

/** Data queue to allow data flow between SimonaAPI and an external simulation */
public final class ExtDataContainerQueue<V extends ExtDataContainer> {
  private final LinkedBlockingDeque<V> receiverTriggerDeque = new LinkedBlockingDeque<>();

  public void queueData(V data) throws InterruptedException {
    receiverTriggerDeque.putLast(data);
  }

  public V takeAll() throws InterruptedException {
    return receiverTriggerDeque.takeFirst();
  }

  public <R> R takeData(Function<V, R> extractor) throws InterruptedException {
    V data = receiverTriggerDeque.takeFirst();
    R result = extractor.apply(data);

    if (!data.isEmpty()) {
      receiverTriggerDeque.putFirst(data);
    }

    return result;
  }
}
