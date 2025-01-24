/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.DataQueueExtSimulationExtSimulator;
import edu.ie3.simona.api.data.ExtDataConnection;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmDataConnection;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultDataConnection;
import edu.ie3.simona.api.exceptions.ExtDataConnectionException;
import edu.ie3.simona.api.simulation.mapping.DataType;
import edu.ie3.simona.api.simulation.mapping.ExtEntityMapping;
import java.util.*;
import java.util.stream.Collectors;
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
  protected final DataQueueExtSimulationExtSimulator<ExtInputDataContainer>
      dataQueueExtCoSimulatorToSimonaApi;

  /** Queue for the data connection from SimonaAPI to the external co-simulation */
  protected final DataQueueExtSimulationExtSimulator<ExtResultContainer>
      dataQueueSimonaApiToExtCoSimulator;

  /** Name of the external co-simulation */
  protected final String extSimulatorName;

  protected ExtCoSimulation(String simulationName, String extSimulatorName) {
    super(simulationName);
    this.extSimulatorName = extSimulatorName;
    this.dataQueueExtCoSimulatorToSimonaApi = new DataQueueExtSimulationExtSimulator<>();
    this.dataQueueSimonaApiToExtCoSimulator = new DataQueueExtSimulationExtSimulator<>();
  }

  @SafeVarargs
  protected static Set<ExtDataConnection> toSet(
      Optional<? extends ExtDataConnection>... optionals) {
    return Arrays.stream(optionals)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
  }

  /**
   * Builds an {@link ExtPrimaryDataConnection}.
   *
   * @param mapping between the external simulation and SIMONA.
   * @param log logger
   * @return an ext primary data connection
   */
  public static ExtPrimaryDataConnection buildPrimaryConnection(
      ExtEntityMapping mapping, Logger log) {
    Map<String, UUID> primaryMapping = mapping.getExtId2UuidMapping(DataType.EXT_PRIMARY_INPUT);

    if (primaryMapping.isEmpty()) {
      log.warn("No primary data connection was created.");
      throw new ExtDataConnectionException(ExtPrimaryDataConnection.class);
    } else {
      log.info("Primary data connection with {} entities created.", primaryMapping.size());
      return new ExtPrimaryDataConnection(primaryMapping);
    }
  }

  /**
   * Builds an {@link ExtEmDataConnection}.
   *
   * @param mapping between the external simulation and SIMONA.
   * @param log logger
   * @return an ext em data connection
   */
  public static ExtEmDataConnection buildEmConnection(ExtEntityMapping mapping, Logger log) {
    Map<String, UUID> emMapping = mapping.getExtId2UuidMapping(DataType.EXT_EM_INPUT);

    if (emMapping.isEmpty()) {
      log.warn("No em data connection was created.");
      throw new ExtDataConnectionException(ExtEmDataConnection.class);
    } else {
      log.info("Em data connection with {} entities created.", emMapping.size());
      return new ExtEmDataConnection(emMapping);
    }
  }

  /**
   * Builds an {@link ExtResultDataConnection}.
   *
   * @param mapping between the external simulation and SIMONA.
   * @param log logger
   * @return an ext result data connection
   */
  public static ExtResultDataConnection buildResultConnection(
      ExtEntityMapping mapping, Logger log) {
    Map<UUID, String> resultParticipantMapping =
        mapping.getExtUuid2IdMapping(DataType.EXT_PARTICIPANT_RESULT);
    Map<UUID, String> resultGridMapping = mapping.getExtUuid2IdMapping(DataType.EXT_GRID_RESULT);
    Map<UUID, String> resultFlexOptionsMapping =
        mapping.getExtUuid2IdMapping(DataType.EXT_FLEX_OPTIONS_RESULT);

    if (resultParticipantMapping.isEmpty()
        && resultGridMapping.isEmpty()
        && resultFlexOptionsMapping.isEmpty()) {
      log.warn("No result connection was created.");
      throw new ExtDataConnectionException(ExtResultDataConnection.class);
    } else {
      log.info(
          "Result connection with {} participants, {} grid assets and {} flex option mappings created.",
          resultParticipantMapping.size(),
          resultGridMapping.size(),
          resultFlexOptionsMapping.size());
      return new ExtResultDataConnection(
          resultParticipantMapping, resultGridMapping, resultFlexOptionsMapping);
    }
  }

  /**
   * Function to send primary data to SIMONA using the given {@link ExtPrimaryDataConnection}. This
   * method will take a value from the {@link #dataQueueExtCoSimulatorToSimonaApi}.
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
    sendPrimaryDataToSimona(
        extPrimaryDataConnection,
        tick,
        dataQueueExtCoSimulatorToSimonaApi.takeData().getSimonaInputMap(),
        maybeNextTick,
        log);
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
      Map<String, Value> dataMap,
      Optional<Long> maybeNextTick,
      Logger log) {
    log.debug("Wait for Primary Data from {}", extSimulatorName);
    log.debug("Received Primary Data from {}", extSimulatorName);
    extPrimaryDataConnection.convertAndSend(tick, dataMap, maybeNextTick, log);
    log.debug("Provided Primary Data to SIMONA!");
  }

  /**
   * Function to send em data to SIMONA using the given {@link ExtEmDataConnection}. This method
   * will take a value from the * {@link #dataQueueExtCoSimulatorToSimonaApi}.
   *
   * <p>{@code nextTick} is necessary, because the em agents have an own scheduler that should know,
   * when the next set point arrives.
   *
   * @param extEmDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param maybeNextTick option for the next tick data is sent
   * @param log logger
   */
  protected void sendEmDataToSimona(
      ExtEmDataConnection extEmDataConnection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    sendEmDataToSimona(
        extEmDataConnection,
        tick,
        dataQueueExtCoSimulatorToSimonaApi.takeData().getSimonaInputMap(),
        maybeNextTick,
        log);
  }

  /**
   * Function to send em data to SIMONA using the given {@link ExtEmDataConnection}.
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
  protected void sendEmDataToSimona(
      ExtEmDataConnection extEmDataConnection,
      long tick,
      Map<String, Value> dataMap,
      Optional<Long> maybeNextTick,
      Logger log) {
    log.debug("Received EmData from {}", extSimulatorName);
    extEmDataConnection.convertAndSend(tick, dataMap, maybeNextTick, log);
    log.debug("Provided EmData to SIMONA!");
  }

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

  private void sendSingleResultType(
      String type,
      Map<String, ModelResultEntity> resultsToBeSend,
      long tick,
      Optional<Long> nextTick,
      Logger log)
      throws InterruptedException {
    log.info("Request results from SIMONA for {} for tick {}!", type, tick);
    log.debug(
        "[{}] Received {} results from SIMONA!\n{}",
        tick,
        type,
        resultMapToString(resultsToBeSend));
    dataQueueSimonaApiToExtCoSimulator.queueData(
        new ExtResultContainer(tick, resultsToBeSend, nextTick));
    log.info("Sent {} results for tick {} to {}", type, tick, extSimulatorName);
  }

  /**
   * Function to send all result data from SIMONA to the external simulation using the given {@link
   * ExtResultDataConnection}
   *
   * @param connection the connection to SIMONA
   * @param tick for which data is received
   * @param maybeNextTick option for the next tick data is received
   * @param log logger
   */
  protected void sendResultToExt(
      ExtResultDataConnection connection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    log.debug("Request results from SIMONA!");
    Map<String, ModelResultEntity> resultsToBeSend = connection.requestResults(tick);
    log.debug("Received results from SIMONA!");
    dataQueueSimonaApiToExtCoSimulator.queueData(
        new ExtResultContainer(tick, resultsToBeSend, maybeNextTick));
    log.debug("Sent results to {}", extSimulatorName);
  }

  private String resultMapToString(Map<String, ModelResultEntity> results) {
    StringBuilder resultString = new StringBuilder();
    for (String key : results.keySet()) {
      resultString
          .append("id = ")
          .append(key)
          .append(", time = ")
          .append(results.get(key).getTime())
          .append(", result = ")
          .append(results.get(key).getClass().getSimpleName())
          .append("\n");
    }
    return resultString.toString();
  }
}
