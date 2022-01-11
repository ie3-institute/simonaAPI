/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ev.ExtEvData;
import edu.ie3.simona.api.data.ev.ExtEvSimulation;
import edu.ie3.simona.api.simulation.ontology.ActivityStartTrigger;
import edu.ie3.simona.api.simulation.ontology.CompletionMessage;
import edu.ie3.simona.api.simulation.ontology.ExtSimMessage;
import edu.ie3.simona.api.simulation.ontology.SimTerminated;
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

    // now we can start the loop
    try {
      boolean simulationFinished = false;
      while (!simulationFinished) {

        // take() will block until an object is ready for us
        ExtSimMessage msg = data.receiveMessageQueue.take();

        if (msg.getClass().equals(ActivityStartTrigger.class)) {
          final ActivityStartTrigger activityStartTrigger = (ActivityStartTrigger) msg;
          List<Long> newTriggers = doActivity(activityStartTrigger.tick()); // this is blocking
          data.send(new CompletionMessage(newTriggers));

          if (newTriggers.isEmpty()) simulationFinished = true;
        } else if (msg.getClass().equals(SimTerminated.class)) {
          terminated();
        } else {
          throw new IllegalArgumentException("Invalid message " + msg + " received.");
        }
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * This method is called for every tick of the external simulation that is triggered.
   *
   * @param tick The current tick
   * @return a list of future ticks at which this external simulation wants to be triggered.
   */
  protected abstract List<Long> doActivity(long tick);

  /** This method is called when the main simulation has terminated successfully or with failure. */
  protected void terminated() {
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
