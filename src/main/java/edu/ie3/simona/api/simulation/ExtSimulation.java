/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.simona.api.data.SetupData;
import edu.ie3.simona.api.data.connection.ExtDataConnection;
import edu.ie3.simona.api.data.connection.ExtSimDataConnection;
import edu.ie3.simona.api.exceptions.ExtSimException;
import edu.ie3.simona.api.ontology.simulation.*;
import java.util.OptionalLong;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Every external simulation must extend this class in order to get triggered by the main
 * simulation.
 */
public abstract class ExtSimulation implements Runnable {

  protected final Logger log;

  protected String simulationName;

  private ExtSimDataConnection dataConnection;

  private SetupData setupData;

  protected ExtSimulation(String simulationName) {
    this.simulationName = simulationName;
    this.log = LoggerFactory.getLogger(simulationName);
  }

  public void run() {
    try {
      // now we can start the loop
      boolean simulationFinished = false;
      while (!simulationFinished) {
        simulationFinished = takeAndHandleMessage();
      }
    } catch (InterruptedException | ExtSimException ie) {
      // Printing the error message
      log.error("Exception thrown in external simulation!", ie);

      // This is the topmost method in the thread call stack,
      // so we handle the exception ourselves
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
  private boolean takeAndHandleMessage() throws ExtSimException, InterruptedException {
    // take() will block until an object is ready for us
    final ControlMessageToExt msg = dataConnection.receive();

    return switch (msg) {
      case ActivationMessage(long tick) -> {
        OptionalLong newTrigger;

        if (tick == -1L) {
          // this is blocking until initialization has finished
          newTrigger = OptionalLong.of(initialize());
        } else {
          // this is blocking until processing of this tick has finished
          newTrigger = doActivity(tick);
        }
        dataConnection.send(new CompletionMessage(newTrigger));

        yield newTrigger.isEmpty();
      }
      case TerminationMessage(boolean simulationSuccessful) -> {
        terminate(simulationSuccessful);
        dataConnection.send(new TerminationCompleted());

        yield true;
      }
    };
  }

  /**
   * This method is called when the external simulation needs to be initialized
   *
   * @return The first regular tick at which this external simulation wants to be triggered, if
   *     applicable.
   */
  protected abstract long initialize();

  /**
   * This method is called for every tick of the external simulation that is triggered.
   *
   * @param tick The current tick
   * @return The next tick at which this external simulation wants to be triggered, if applicable.
   * @throws ExtSimException if any exception was thrown in the external simulation
   * @throws InterruptedException if the thread running this has been interrupted (possibly during
   *     blocking)
   */
  protected abstract OptionalLong doActivity(long tick)
      throws ExtSimException, InterruptedException;

  /**
   * This method is called when the main simulation wants to terminate.
   *
   * @param simulationSuccessful Whether the simulation was run successfully or has ended with an
   *     error
   */
  protected void terminate(boolean simulationSuccessful) {
    // to be overwritten in subclass
  }

  /**
   * Method to set the external simulation data connection.
   *
   * @param dataConnection to set up
   */
  public final void setDataConnection(ExtSimDataConnection dataConnection) {
    this.dataConnection = dataConnection;
  }

  /**
   * Method to provide the external simulation with setup data.
   *
   * @param setupData that is provided
   */
  public final void setSetupData(SetupData setupData) {
    this.setupData = setupData;
  }

  /** Returns the {@link SetupData} of this external simulation. */
  public final SetupData getSetupData() {
    return setupData;
  }

  /** Returns the name of this external simulation. */
  public final String getSimulationName() {
    return simulationName;
  }

  /** Returns all {@link ExtDataConnection} of this simulation. */
  public abstract Set<ExtDataConnection> getDataConnections();
}
