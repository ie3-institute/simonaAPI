/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.simona.api.data.SetupData;
import edu.ie3.simona.api.data.connection.ExtDataConnection;
import edu.ie3.simona.api.data.connection.ExtSimDataConnection;
import edu.ie3.simona.api.ontology.simulation.*;
import java.util.Optional;
import java.util.Set;

/**
 * Every external simulation must extend this class in order to get triggered by the main
 * simulation.
 */
public abstract class ExtSimulation implements Runnable {

  protected String simulationName;

  private ExtSimDataConnection dataConnection;

  private SetupData setupData;

  protected ExtSimulation(String simulationName) {
    this.simulationName = simulationName;
  }

  public void run() {
    try {
      // now we can start the loop
      boolean simulationFinished = false;
      while (!simulationFinished) {
        simulationFinished = takeAndHandleMessage();
      }
    } catch (InterruptedException ie) {
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
  private boolean takeAndHandleMessage() throws InterruptedException {
    // take() will block until an object is ready for us
    final ControlMessageToExt msg = dataConnection.receive();

    if (msg.getClass().equals(ActivationMessage.class)) {
      final ActivationMessage activationMessage = (ActivationMessage) msg;
      Optional<Long> newTrigger;

      if (activationMessage.tick() == -1L) {
        // this is blocking until initialization has finished
        newTrigger = Optional.of(initialize());
      } else {
        // this is blocking until processing of this tick has finished
        newTrigger = doActivity(activationMessage.tick());
      }
      dataConnection.send(new CompletionMessage(newTrigger));

      return newTrigger.isEmpty();
    } else if (msg.getClass().equals(TerminationMessage.class)) {
      final TerminationMessage terminationMsg = (TerminationMessage) msg;
      terminate(terminationMsg.simulationSuccessful());
      dataConnection.send(new TerminationCompleted());

      return true;
    } else {
      throw new IllegalArgumentException("Invalid message " + msg + " received.");
    }
  }

  /**
   * This method is called when the external simulation needs to be initialized
   *
   * @return The first regular tick at which this external simulation wants to be triggered, if
   *     applicable.
   */
  protected abstract Long initialize();

  /**
   * This method is called for every tick of the external simulation that is triggered.
   *
   * @param tick The current tick
   * @return The next tick at which this external simulation wants to be triggered, if applicable.
   */
  protected abstract Optional<Long> doActivity(long tick);

  /**
   * This method is called when the main simulation wants to terminate.
   *
   * @param simulationSuccessful Whether the simulation was run successfully or has ended with an
   *     error
   */
  protected void terminate(Boolean simulationSuccessful) {
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
