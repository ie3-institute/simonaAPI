/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.simona.api.data.container.ExtInputContainer;
import edu.ie3.simona.api.data.container.ExtOutputContainer;
import java.util.Queue;

/**
 * Interface that should be implemented by the simulator or a similar class of the external
 * co-simulation framework. This class needs to be provided to the {@link ExtCoSimulation} to
 * connect the external co-simulation framework to SIMONA.
 *
 * @param <I> Type of the initialization data.
 */
public interface ExtCoSimFramework<I extends ExtCoSimFramework.InitData> {

  /** Returns the name of the external simulator. */
  String getName();

  /**
   * Provides access to the internal initialization queue of the {@link ExtCoSimulation}.
   *
   * @param initDataQueue The queue.
   */
  void setInitDataQueue(Queue<I> initDataQueue);

  /**
   * Method to retrieve the current status of the external co-simulation framework.
   *
   * @param simonaTick The current tick of SIMONA.
   * @return The current status of the external framework.
   * @throws InterruptedException If a problem occurs.
   */
  Status getStatus(long simonaTick) throws InterruptedException;

  /**
   * Method to provide new output data from SIMONA to the external co-simulation framework.
   *
   * @param outputData That should be provided.
   */
  void provideOutputData(ExtOutputContainer outputData);

  /**
   * Method to signal the {@link ExtCoSimFramework} to go to the next tick. Called in response to
   * {@link SimonaIsAhead}.
   *
   * @param simonaTick The next tick for which SIMONA can handle data.
   */
  void goToNextTick(long simonaTick);

  /** Interface defining different states the {@link ExtCoSimFramework} can return. */
  sealed interface Status permits Finished, HasData, SimonaIsAhead, SimonaIsBehind {}

  /**
   * Status declaring that new input data is available.
   *
   * @param container The new input data.
   */
  record HasData(ExtInputContainer container) implements Status {}

  /**
   * Status declaring that SIMONA is behind of the {@link ExtCoSimFramework}.
   *
   * @param extTick The current tick of the external co-simulation.
   */
  record SimonaIsBehind(long extTick) implements Status {}

  /** Status declaring that SIMONA is ahead of the {@link ExtCoSimFramework}. */
  record SimonaIsAhead() implements Status {}

  /** Status declaring that the {@link ExtCoSimFramework} has finished. */
  record Finished() implements Status {}

  /** Interface for the initialization data. */
  interface InitData {}

  /**
   * Converter that can be used if the {@link ExtCoSimFramework} uses a different timescale than 1s.
   *
   * @param scalingFactor That will be used for conversion.
   */
  record TickConverter(double scalingFactor) {
    /**
     * Method to convert the external tick to SIMONA's timescale.
     *
     * @param extTick The external tick that should be converted.
     * @return The converted tick.
     */
    public long toSimonaTick(long extTick) {
      return (long) (extTick * scalingFactor);
    }

    /**
     * Method to convert the SIMONA tick to the external timescale.
     *
     * @param simonaTick That should be converted.
     * @return The converted tick.
     */
    public long toExtTick(long simonaTick) {
      return (long) (simonaTick / scalingFactor);
    }
  }
}
