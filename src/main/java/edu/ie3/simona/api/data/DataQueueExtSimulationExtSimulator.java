package edu.ie3.simona.api.data;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Data queue to allow data flow between SIMONA and a co-simulator
 */
public class DataQueueExtSimulationExtSimulator<V extends ExtDataPackage> {
    public final LinkedBlockingQueue<V> receiverTriggerQueue = new LinkedBlockingQueue<>();

    public DataQueueExtSimulationExtSimulator() {}

    public void queueData(V data) throws InterruptedException {
        this.receiverTriggerQueue.put(data);
    }

    public V takeData() throws InterruptedException {
        return this.receiverTriggerQueue.take();
    }
}
