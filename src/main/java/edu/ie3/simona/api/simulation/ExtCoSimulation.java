/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import static java.util.Collections.emptyList;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtDataContainerQueue;
import edu.ie3.simona.api.data.container.ExtInputDataContainer;
import edu.ie3.simona.api.data.container.ExtResultContainer;
import edu.ie3.simona.api.data.em.EmMode;
import edu.ie3.simona.api.data.em.ExtEmDataConnection;
import edu.ie3.simona.api.data.em.ontology.*;
import edu.ie3.simona.api.data.mapping.DataType;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.results.ExtResultDataConnection;
import edu.ie3.simona.api.exceptions.ExtDataConnectionException;
import java.util.*;
import org.slf4j.Logger;

/**
 * Abstract class for an external co-simulation with the structure: external api - ext-co-simulation
 * - ext-simulation - simonaAPI - simona
 *
 * <p>It contains all function to transfer primary data and em data to SIMONA and results to the
 * external co-simulation.
 */
public abstract class ExtCoSimulation extends ExtSimulation {

  /** Queue for the data connection from the external co-simulation to SimonaAPI */
  protected final ExtDataContainerQueue<ExtInputDataContainer> queueToSimona;

  /** Queue for the data connection from SimonaAPI to the external co-simulation */
  protected final ExtDataContainerQueue<ExtResultContainer> queueToExt;

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
      List<UUID> controlled, EmMode mode, Logger log) {
    if (controlled.isEmpty()) {
      log.warn("Em data connection with 0 controlled entities created. This might lead to errors!");
    } else {
      log.info("Em data connection with {} controlled entities created.", controlled.size());
    }

    log.info("Em mode: {}", mode);

    return new ExtEmDataConnection(controlled, mode);
  }

  /**
   * Builds an {@link ExtResultDataConnection}.
   *
   * @param mapping between the external simulation and SIMONA.
   * @param log logger
   * @return an ext result data connection
   */
  public static ExtResultDataConnection buildResultConnection(
      Map<DataType, List<UUID>> mapping, Logger log) {
    List<UUID> participantResults =
        mapping.getOrDefault(DataType.EXT_PARTICIPANT_RESULT, emptyList());
    List<UUID> gridResults = mapping.getOrDefault(DataType.EXT_GRID_RESULT, emptyList());
    List<UUID> flexResults = mapping.getOrDefault(DataType.EXT_FLEX_OPTIONS_RESULT, emptyList());

    if (participantResults.isEmpty() && gridResults.isEmpty() && flexResults.isEmpty()) {
      log.warn("No result connection was created.");
      throw new ExtDataConnectionException(ExtResultDataConnection.class);
    } else {
      log.info(
          "Result connection with {} participants, {} grid assets and {} flex option mappings created.",
          participantResults.size(),
          gridResults.size(),
          flexResults.size());
      return new ExtResultDataConnection(participantResults, gridResults, flexResults);
    }
  }

  private void checkTick(long expectedTick) throws InterruptedException {
    long dataTick = queueToSimona.takeData(ExtInputDataContainer::getTick);

    if (dataTick != expectedTick) {
      throw new RuntimeException(
          String.format(
              "Provided input data for tick %d, but SIMONA expects input data for tick %d",
              dataTick, expectedTick));
    }
  }

  // primary data methods

  /**
   * Function to send primary data to SIMONA using the given {@link ExtPrimaryDataConnection}. This
   * method will take a value from the {@link #queueToSimona}.
   *
   * @param extPrimaryDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param maybeNextTick option for the next tick data is sent
   * @param log logger
   */
  protected void sendPrimaryDataToSimona(
      ExtPrimaryDataConnection extPrimaryDataConnection,
      long tick,
      Optional<Long> maybeNextTick,
      Logger log)
      throws InterruptedException {
    checkTick(tick);
    Map<UUID, Value> inputData = queueToSimona.takeData(ExtInputDataContainer::extractPrimaryData);
    sendPrimaryDataToSimona(extPrimaryDataConnection, tick, inputData, maybeNextTick, log);
  }

  /**
   * Function to send primary data to SIMONA using the given {@link ExtPrimaryDataConnection}.
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
   * @param maybeNextTick option for the next tick data is sent
   * @param log logger
   * @throws InterruptedException if the fetching of data is interrupted
   */
  protected void sendEmSetPointsToSimona(
      ExtEmDataConnection extEmDataConnection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    checkTick(tick);
    Map<UUID, PValue> inputData = queueToSimona.takeData(ExtInputDataContainer::extractSetPoints);

    sendEmSetPointsToSimona(extEmDataConnection, tick, inputData, maybeNextTick, log);
  }

  /**
   * Function to send em set point data to SIMONA using the given {@link ExtEmDataConnection}.
   *
   * <p>{@code nextTick} is necessary, because the em agents have an own scheduler that should know,
   * when the next set point arrives.
   *
   * @param extEmDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param dataMap map: id to value
   * @param maybeNextTick option for the next tick data is sent
   * @param log logger
   */
  protected void sendEmSetPointsToSimona(
      ExtEmDataConnection extEmDataConnection,
      long tick,
      Map<UUID, PValue> dataMap,
      Optional<Long> maybeNextTick,
      Logger log) {
    log.debug("Received em set points from {}", extSimulatorName);
    extEmDataConnection.sendSetPoints(tick, dataMap, maybeNextTick, log);
    log.debug("Provided em set points to SIMONA!");
  }

  protected void useFlexCommunication(
      ExtEmDataConnection extEmDataConnection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    // handle flex requests
    boolean notFinished = true;

    while (notFinished) {

      long extTick = queueToSimona.takeData(ExtInputDataContainer::getTick);

      log.warn("Current simulator tick: {}, SIMONA tick: {}", extTick, tick);

      if (tick == extTick) {
        ExtInputDataContainer container = queueToSimona.takeAll();

        log.warn("Flex requests: {}", container.flexRequestsString());
        log.warn("Flex options: {}", container.flexOptionsString());
        log.warn("Set points: {}", container.setPointsString());

        // send received data to SIMONA
        var requests = container.extractFlexRequests();
        var options = container.extractFlexOptions();
        var setPoints = container.extractSetPoints();

        extEmDataConnection.sendFlexRequests(tick, requests, maybeNextTick, log);

        extEmDataConnection.sendFlexOptions(tick, options, maybeNextTick, log);

        extEmDataConnection.sendSetPoints(tick, setPoints, maybeNextTick, log);

        log.warn("Unhandled flex requests: {}", container.flexRequestsString());
        log.warn("Unhandled flex options: {}", container.flexOptionsString());
        log.warn("Unhandled set points: {}", container.setPointsString());

        if (requests.isEmpty() && options.isEmpty() && setPoints.isEmpty()) {
          log.info("Requesting a service completion for tick: {}.", tick);
          extEmDataConnection.requestCompletion(tick);
        }

      } else {
        notFinished = false;

        log.info("External simulator finished tick {}. Request completion.", tick);
        extEmDataConnection.requestCompletion(tick);
      }

      EmDataResponseMessageToExt received = extEmDataConnection.receiveAny();

      Map<UUID, ResultEntity> results = new HashMap<>();

      if (received instanceof EmCompletion) {
        notFinished = false;
        log.info("Finished for tick: {}", tick);

      } else if (received instanceof FlexRequestResponse flexRequestResponse) {
        results.putAll(flexRequestResponse.flexRequests());

      } else if (received instanceof FlexOptionsResponse flexOptionsResponse) {
        results.putAll(flexOptionsResponse.receiverToFlexOptions());

      } else if (received instanceof EmSetPointDataResponse setPointDataResponse) {
        results.putAll(setPointDataResponse.emData());

      } else {
        log.warn("Received unsupported data response: {}", received);
      }

      log.warn("Results to ext: {}", results);

      ExtResultContainer resultContainer = new ExtResultContainer(tick, results, maybeNextTick);

      queueToExt.queueData(resultContainer);
    }
  }

  // result data methods

  /**
   * Function to send only participant result data from SIMONA to the external simulation using the
   * given {@link ExtResultDataConnection}
   */
  protected void sendParticipantResultsToExt(
      ExtResultDataConnection connection, long tick, Optional<Long> nextTick, Logger log)
      throws InterruptedException {
    sendSingleResultType(
        "participant", connection.requestParticipantResults(tick), tick, nextTick, log);
  }

  /**
   * Function to send only grid result data from SIMONA to the external simulation using the given
   * {@link ExtResultDataConnection}
   */
  protected void sendGridResultsToExt(
      ExtResultDataConnection connection, long tick, Optional<Long> nextTick, Logger log)
      throws InterruptedException {
    sendSingleResultType("grid", connection.requestGridResults(tick), tick, nextTick, log);
  }

  /**
   * Function to send only flex option result data from SIMONA to the external simulation using the
   * given {@link ExtResultDataConnection}
   */
  protected void sendFlexOptionResultsToExt(
      ExtResultDataConnection connection, long tick, Optional<Long> nextTick, Logger log)
      throws InterruptedException {
    sendSingleResultType(
        "flex option", connection.requestFlexOptionResults(tick), tick, nextTick, log);
  }

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
    Map<UUID, ResultEntity> resultsToBeSend = connection.requestResults(tick);
    log.debug("Received results from SIMONA!");
    queueToExt.queueData(new ExtResultContainer(tick, resultsToBeSend, maybeNextTick));
    log.debug("Sent results to {}", extSimulatorName);
  }

  private void sendSingleResultType(
      String type,
      Map<UUID, ResultEntity> resultsToBeSend,
      long tick,
      Optional<Long> nextTick,
      Logger log)
      throws InterruptedException {
    log.info("Request results from SIMONA for {} for tick {}!", type, tick);

    queueToExt.queueData(new ExtResultContainer(tick, resultsToBeSend, nextTick));
    log.info("Sent {} results for tick {} to {}", type, tick, extSimulatorName);
  }
}
