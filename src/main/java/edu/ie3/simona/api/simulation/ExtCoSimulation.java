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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstract class for an external co-simulation with bidirectional communication with SIMONA.
 *
 * <p>It contains functions to simplify the transfer of primary data and em data to SIMONA and
 * results to the external co-simulation.
 */
public abstract class ExtCoSimulation extends ExtSimulation {

  protected final Logger log;

  /**
   * The external co-simulation framework.
   */
  protected final ExtCoSimFramework externalCoSimFramework;
  private final LinkedBlockingQueue<ExtCoSimFramework.InitData> initDataQueue = new LinkedBlockingQueue<>();

  protected ExtCoSimulation(String simulationName, ExtCoSimFramework extCoSimFramework) {
    super(simulationName);
    log = LoggerFactory.getLogger(simulationName);
    this.externalCoSimFramework = extCoSimFramework;
    externalCoSimFramework.setInitDataQueue(initDataQueue);
  }

  @Override
  protected final Optional<Long> doActivity(long tick) {
    try {
      log.info(
              "+++++++++++++++++++++++++++ Activities in External simulation: Tick {} has been triggered. +++++++++++++++++++++++++++",
              tick);

      Optional<Long> maybeNextTick = Optional.of(Long.MAX_VALUE);
      boolean run = true;

      while (run && continueActivity(tick)) {
        Optional<Long> newTickOption = switch(externalCoSimFramework.getStatus(tick)) {
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
            yield Optional.of(extTick);
          }
          case ExtCoSimFramework.SimonaIsAhead() -> {
            externalCoSimFramework.goToNextTick(tick);

            yield maybeNextTick;
          }
          case ExtCoSimFramework.Finished() -> {
            finishSimulation(tick);
            yield Optional.empty();
          }
        };



        maybeNextTick = getNextTickOption(maybeNextTick, newTickOption);
        log.info("Updated next tick option: {}", maybeNextTick);
      }

      log.info(
              "++++++++++++++++++++++ Activities in External simulation finished for tick {}. Next tick option: {} ++++++++++++++++++++++",
              tick,
              maybeNextTick);

      return maybeNextTick;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected final <R extends ExtCoSimFramework.InitData> R getInitData(Class<R> clazz) throws InterruptedException {
    ExtCoSimFramework.InitData initialisationData = initDataQueue.take();
    if (clazz.isAssignableFrom(initialisationData.getClass())) {
      return clazz.cast(initialisationData);
    } else {
      throw new IllegalStateException(
              "Received unexpected initialisation data: " + initialisationData);
    }
  }

  public abstract ExtOutputContainer handleExternalData(ExtInputContainer inputData) throws Exception;

  public abstract ExtOutputContainer handleNoExternalData(long tick) throws Exception;


  public abstract void finishSimulation(long tick) throws Exception;

  public abstract long determineNextTick(long tick);

  public abstract boolean continueActivity(long tick);

  protected final Optional<Long> getNextTickOption(Optional<Long> maybeNextTick, Optional<Long> otherOption) {
    if (maybeNextTick.isEmpty()) {
      return otherOption;
    }

    if (otherOption.isEmpty()) {
      return maybeNextTick;
    }

    long nextTick = maybeNextTick.get();
    long other = otherOption.get();

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
