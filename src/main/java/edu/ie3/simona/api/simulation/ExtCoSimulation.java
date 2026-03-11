/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.connection.ExtEmDataConnection;
import edu.ie3.simona.api.data.connection.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.connection.ExtResultDataConnection;
import edu.ie3.simona.api.data.container.ExtInputContainer;
import edu.ie3.simona.api.data.container.ExtOutputContainer;
import edu.ie3.simona.api.exceptions.ExtDataConnectionException;
import edu.ie3.simona.api.simulation.ExtCoSimFramework.InitData;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for an external co-simulation with bidirectional communication with SIMONA.
 *
 * <p>It contains functions to simplify the transfer of primary data and em data to SIMONA and
 * results to the external co-simulation.
 */
public abstract class ExtCoSimulation<I extends InitData> extends ExtSimulation {

  protected final Logger log;

  /** The external co-simulation framework. */
  protected final ExtCoSimFramework<I> externalCoSimFramework;

  private final LinkedBlockingQueue<I> initDataQueue = new LinkedBlockingQueue<>();

  protected ExtCoSimulation(String simulationName, ExtCoSimFramework<I> extCoSimFramework) {
    super(simulationName);
    log = LoggerFactory.getLogger(simulationName);
    this.externalCoSimFramework = extCoSimFramework;
    externalCoSimFramework.setInitDataQueue(initDataQueue);
  }

  @Override
  protected final OptionalLong doActivity(long tick) {
    try {
      log.info(
          "+++++++++++++++++++++++++++ Activities in External simulation: Tick {} has been triggered. +++++++++++++++++++++++++++",
          tick);

      OptionalLong maybeNextTick = OptionalLong.empty();
      boolean run = true;

      do {
        OptionalLong newTickOption =
            switch (externalCoSimFramework.getStatus(tick)) {
              case ExtCoSimFramework.HasData(ExtInputContainer container) -> {
                ExtOutputContainer result;

                if (container.isEmpty()) {
                  // handle no data provided
                  result = handleNoExternalData(container.getTick());
                } else {
                  // handle external data
                  result = handleExternalData(container);
                }

                externalCoSimFramework.provideOutputData(result);
                yield result.getMaybeNextTick();
              }
              case ExtCoSimFramework.SimonaIsBehind(long extTick) -> {
                run = false;
                yield OptionalLong.of(extTick);
              }
              case ExtCoSimFramework.SimonaIsAhead() -> {
                externalCoSimFramework.goToNextTick(tick);

                yield maybeNextTick;
              }
              case ExtCoSimFramework.Finished() -> {
                finishSimulation(tick);
                yield OptionalLong.empty();
              }
            };

        maybeNextTick = getNextTickOption(maybeNextTick, newTickOption);
        log.debug("Updated next tick option: {}", maybeNextTick);
      } while (run && continueActivity(tick));

      log.info(
          "++++++++++++++++++++++ Activities in External simulation finished for tick {}. Next tick option: {} ++++++++++++++++++++++",
          tick,
          maybeNextTick);

      return maybeNextTick;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Method for retrieving the initialization data from the internal queue.
   *
   * @param clazz Class used to specify which object to look for.
   * @return The object.
   * @param <R> Type of the object that should be returned.
   * @throws InterruptedException If the thread is interrupted.
   */
  protected final <R extends I> R getInitData(Class<R> clazz) throws InterruptedException {
    InitData initialisationData = initDataQueue.take();
    if (clazz.isAssignableFrom(initialisationData.getClass())) {
      return clazz.cast(initialisationData);
    } else {
      throw new IllegalStateException(
          "Received unexpected initialisation data: " + initialisationData);
    }
  }

  /**
   * Method that is called to handle data from the {@link ExtCoSimFramework}.
   *
   * @param inputData Container containing the input data as well as the current tick.
   * @return An output container that contains the result that should be provided to the external
   *     co-simulation.
   * @throws InterruptedException If the thread is interrupted.
   */
  public abstract ExtOutputContainer handleExternalData(ExtInputContainer inputData)
      throws InterruptedException;

  /**
   * Method that is called if the {@link ExtCoSimFramework} is providing no input data for the tick.
   *
   * @param tick For which no data was provided.
   * @return An output container that contains the result that should be provided to the external
   *     co-simulation.
   * @throws InterruptedException If the thread is interrupted.
   */
  public abstract ExtOutputContainer handleNoExternalData(long tick) throws InterruptedException;

  /**
   * Method that is called if the {@link ExtCoSimFramework} sends a finishing message.
   *
   * @param tick For which operations needs to be performed.
   * @throws InterruptedException If the thread is interrupted.
   */
  public abstract void finishSimulation(long tick) throws InterruptedException;

  /**
   * Method to determine the next tick for SIMONA.
   *
   * @param tick The current tick.
   * @return The next tick.
   */
  public abstract long determineNextTick(long tick);

  /**
   * Method to determine if the simulation should stay at the current tick and continue with the
   * activity.
   *
   * @param tick The current tick.
   * @return Whether to stay at the current tick.
   */
  public abstract boolean continueActivity(long tick);

  /**
   * Method to determine the next tick option from two options.
   *
   * @param maybeNextTick The current option.
   * @param otherOption The new option.
   * @return An option that contains the earliest tick considering both options.
   */
  protected final OptionalLong getNextTickOption(
      OptionalLong maybeNextTick, OptionalLong otherOption) {
    if (maybeNextTick.isEmpty()) {
      return otherOption;
    }

    if (otherOption.isEmpty()) {
      return maybeNextTick;
    }

    long nextTick = maybeNextTick.getAsLong();
    long other = otherOption.getAsLong();

    if (other < nextTick && other > 0) {
      return otherOption;
    } else {
      return maybeNextTick;
    }
  }

  // connection helper methods

  /**
   * Builds an {@link ExtPrimaryDataConnection}.
   *
   * @param assetToValueClasses between primary asset and its value class.
   * @param log logger
   * @return an ext primary data connection
   */
  public static ExtPrimaryDataConnection buildPrimaryConnection(
      Map<UUID, Class<? extends Value>> assetToValueClasses, Logger log) {

    if (assetToValueClasses.isEmpty()) {
      log.warn("No primary data connection was created.");
      throw new ExtDataConnectionException(ExtPrimaryDataConnection.class);
    } else {
      log.info("Primary data connection with {} entities created.", assetToValueClasses.size());

      return new ExtPrimaryDataConnection(assetToValueClasses);
    }
  }

  /**
   * Builds an {@link ExtEmDataConnection}.
   *
   * @param controlled uuids for controlled em agents.
   * @param log logger
   * @return an ext em data connection
   */
  public static ExtEmDataConnection buildEmConnection(
      List<UUID> controlled, ExtEmDataConnection.EmMode mode, Logger log) {
    if (controlled.isEmpty()) {
      log.warn("Em data connection with 0 controlled entities created. This might lead to errors!");
      throw new ExtDataConnectionException(ExtEmDataConnection.class);
    } else {
      log.info(
          "Em data connection with mode '{}' and {} controlled entities created.",
          mode,
          controlled.size());

      return new ExtEmDataConnection(controlled, mode);
    }
  }

  /**
   * Builds an {@link ExtResultDataConnection}.
   *
   * @param resultEntities of assets that should send their results
   * @param log logger
   * @return an ext result data connection
   */
  public static ExtResultDataConnection buildResultConnection(
      List<UUID> resultEntities, Logger log) {
    if (resultEntities.isEmpty()) {
      log.warn("No result connection was created.");
      throw new ExtDataConnectionException(ExtResultDataConnection.class);
    } else {
      log.info("Result connection with {} result entities created.", resultEntities.size());
      return new ExtResultDataConnection(resultEntities);
    }
  }
}
