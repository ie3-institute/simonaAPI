/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ev.ExtEvData;
import edu.ie3.simona.api.data.ev.ExtEvSimulation;
import edu.ie3.simona.api.simulation.ontology.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Every external simulation must extend this class in order to get triggered by the main
 * simulation.
 */
public abstract class ExtSimulation implements Runnable {

  private ExtSimAdapterData data;

  protected ExtSimulation() {}

  public void run() {
    try {
      // now we can start the loop
      boolean simulationFinished = false;
      while (!simulationFinished) {
        simulationFinished = takeAndHandleMessage();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Blocks until the next message is received and handles it.
   *
   * @return true if simulation is terminated after handling this message
   * @throws InterruptedException if the thread running this has been interrupted (possibly during
   *     blocking)
   */
  private boolean takeAndHandleMessage() throws InterruptedException {
    // take() will block until an object is ready for us
    final ExtSimMessage msg = data.receiveMessageQueue.take();

    if (msg.getClass().equals(ActivityStartTrigger.class)) {
      final ActivityStartTrigger activityStartTrigger = (ActivityStartTrigger) msg;
      List<Long> newTriggers;

      if (activityStartTrigger.tick() == -1L) {
        newTriggers = initialize(); // this is blocking until initialization has finished
      } else {
        newTriggers =
            doActivity(
                activityStartTrigger
                    .tick()); // this is blocking until processing of this tick has finished
      }
      data.send(new CompletionMessage(newTriggers));

      return newTriggers.isEmpty();
    } else if (msg.getClass().equals(Terminate.class)) {
      final Terminate terminateMsg = (Terminate) msg;
      terminate(terminateMsg.simulationSuccessful());
      data.send(new TerminationCompleted());

      return true;
    } else {
      throw new IllegalArgumentException("Invalid message " + msg + " received.");
    }
  }

  /**
   * This method is called when the external simulation needs to be initialized
   *
   * @return a list of future ticks at which this external simulation wants to be triggered.
   */
  protected abstract List<Long> initialize();

  /**
   * This method is called for every tick of the external simulation that is triggered.
   *
   * @param tick The current tick
   * @return a list of future ticks at which this external simulation wants to be triggered.
   */
  protected abstract List<Long> doActivity(long tick);

  /**
   * This method is called when the main simulation wants to terminate.
   *
   * @param simulationSuccessful whether the simulation was run successfully or has ended with an
   *     error
   */
  protected void terminate(Boolean simulationSuccessful) {
    // to be overwritten in subclass
  }

  public final List<Class<? extends ExtData>> getRequiredAdapters() {
    ArrayList<Class<? extends ExtData>> classes = new ArrayList<>();

    if (this instanceof ExtEvSimulation) classes.add(ExtEvData.class);

    return classes;
  }

  public final void setup(ExtSimAdapterData data, List<ExtData> adapters) {
    this.data = data;

    // todo sanity check if all required data is available
    for (ExtData adapter : adapters) {
      if (adapter instanceof ExtEvData && this instanceof ExtEvSimulation)
        ((ExtEvSimulation) this).setExtEvData((ExtEvData) adapter);
    }
  }

  /**
   * Provides the program arguments that the main simulation was started with
   *
   * @return the main args
   */
  protected String[] getMainArgs() {
    return data.getMainArgs();
  }
}
