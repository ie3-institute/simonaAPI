/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import java.util.concurrent.LinkedBlockingQueue;

/** Data queue to allow data flow between SIMONA and an external simulation */
public class DataQueueExtSimulationExtSimulator<V extends ExtDataContainer> {
  private final LinkedBlockingQueue<V> receiverTriggerQueue = new LinkedBlockingQueue<>();

  public void queueData(V data) throws InterruptedException {
    this.receiverTriggerQueue.put(data);
  }

  public V takeData() throws InterruptedException {
    return this.receiverTriggerQueue.take();
  }
}
