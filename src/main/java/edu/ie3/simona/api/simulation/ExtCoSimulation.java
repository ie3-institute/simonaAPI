/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtDataContainerQueue;
import edu.ie3.simona.api.data.connection.ExtEmDataConnection;
import edu.ie3.simona.api.data.connection.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.connection.ExtResultDataConnection;
import edu.ie3.simona.api.data.container.ExtInputContainer;
import edu.ie3.simona.api.data.container.ExtOutputContainer;
import edu.ie3.simona.api.data.model.em.EmSetPoint;
import edu.ie3.simona.api.exceptions.ExtDataConnectionException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;

/**
 * Abstract class for an external co-simulation with bidirectional communication with SIMONA.
 *
 * <p>It contains functions to simplify the transfer of primary data and em data to SIMONA and
 * results to the external co-simulation.
 */
public abstract class ExtCoSimulation extends ExtSimulation {

  /** Queue for the data connection from the external co-simulation to SimonaAPI */
  protected final ExtDataContainerQueue<ExtInputContainer> queueToSimona;

  /** Queue for the data connection from SimonaAPI to the external co-simulation */
  protected final ExtDataContainerQueue<ExtOutputContainer> queueToExt;

  /** Name of the external co-simulation */
  protected final String extSimulatorName;

  protected ExtCoSimulation(String simulationName, String extSimulatorName) {
    super(simulationName);
    this.extSimulatorName = extSimulatorName;
    this.queueToSimona = new ExtDataContainerQueue<>();
    this.queueToExt = new ExtDataContainerQueue<>();
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

  // primary data methods

  /**
   * Function to send primary data to SIMONA using ExtPrimaryData
   *
   * @param extPrimaryDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param dataMap map: id to value
   * @param maybeNextTick option for the next tick data is sent
   * @param log logger
   */
  protected void sendPrimaryDataToSimona(
      ExtPrimaryDataConnection extPrimaryDataConnection,
      long tick,
      Map<UUID, Value> dataMap,
      Optional<Long> maybeNextTick,
      Logger log) {
    log.debug("Wait for Primary Data from {}", extSimulatorName);
    log.debug("Received Primary Data from {}", extSimulatorName);
    extPrimaryDataConnection.sendPrimaryData(tick, dataMap, maybeNextTick, log);
    log.debug("Provided Primary Data to SIMONA!");
  }

  // energy management data methods

  /**
   * Function to send em set point data to SIMONA using the given {@link ExtEmDataConnection}. This
   * method will take a value from the {@link #queueToSimona}.
   *
   * <p>{@code nextTick} is necessary, because the em agents have an own scheduler that should know,
   * when the next set point arrives.
   *
   * @param extEmDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param log logger
   * @throws InterruptedException if the fetching of data is interrupted
   */
  protected void sendEmSetPointsToSimona(
      ExtEmDataConnection extEmDataConnection, long tick, Logger log) throws InterruptedException {
    Map<UUID, EmSetPoint> inputData = queueToSimona.takeData(ExtInputContainer::extractSetPoints);

    sendEmSetPointsToSimona(extEmDataConnection, tick, inputData, log);
  }

  /**
   * Function to send em data to SIMONA using ExtPrimaryData nextTick is necessary, because the em
   * agents have an own scheduler that should know, when the next set point arrives.
   *
   * @param extEmDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param setPoints map: id to set point
   * @param log logger
   */
  protected void sendEmSetPointsToSimona(
      ExtEmDataConnection extEmDataConnection,
      long tick,
      Map<UUID, EmSetPoint> setPoints,
      Logger log) {
    log.debug("Received em set points from {}", extSimulatorName);
    boolean wasSent = extEmDataConnection.sendEmData(tick, setPoints, log);
    if (!wasSent) {
      log.debug("No set point data was sent to SIMONA!");
    }
    log.debug("Provided em set points to SIMONA!");
  }

  // result data methods

  /**
   * Function to send all result data from SIMONA to the external simulation using the given {@link
   * ExtResultDataConnection}
   *
   * @param connection the connection to SIMONA
   * @param tick for which data is received
   * @param maybeNextTick option for the next tick data is received
   * @param log logger
   * @throws InterruptedException if the fetching of data is interrupted
   */
  protected void sendResultToExt(
      ExtResultDataConnection connection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    log.debug("Request results from SIMONA!");
    Map<UUID, List<ResultEntity>> resultsToBeSend = connection.requestResults(tick);
    log.debug("Received results from SIMONA!");
    ExtOutputContainer container = new ExtOutputContainer(tick, maybeNextTick);
    container.addResults(resultsToBeSend);
    queueToExt.queueData(container);
    log.debug("Sent results to {}", extSimulatorName);
  }
}
