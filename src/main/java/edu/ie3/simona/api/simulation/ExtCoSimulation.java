/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.DataQueueExtSimulationExtSimulator;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmDataConnection;
import edu.ie3.simona.api.data.em.model.FlexOptionRequestValue;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultDataConnection;
import edu.ie3.simona.api.exceptions.ExtDataConnectionException;
import edu.ie3.simona.api.simulation.mapping.DataType;
import edu.ie3.simona.api.simulation.mapping.ExtEntityEntry;
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

  // connection helper methods

  /**
   * Builds an {@link ExtPrimaryDataConnection}.
   *
   * @param mapping between the external simulation and SIMONA.
   * @param log logger
   * @return an ext primary data connection
   */
  @SuppressWarnings("unchecked")
  public static ExtPrimaryDataConnection buildPrimaryConnection(
      ExtEntityMapping mapping, Logger log) {
    List<ExtEntityEntry> entries = mapping.getExtEntityEntries(DataType.EXT_PRIMARY_INPUT);

    if (entries.isEmpty()) {
      log.warn("No primary data connection was created.");
      throw new ExtDataConnectionException(ExtPrimaryDataConnection.class);
    } else {
      log.info("Primary data connection with {} entities created.", entries.size());

      Map<String, UUID> primaryMapping =
          entries.stream().collect(Collectors.toMap(ExtEntityEntry::id, ExtEntityEntry::uuid));
      Map<UUID, Class<Value>> valueClasses = new HashMap<>();
      entries.stream()
          .filter(e -> e.columnScheme().isPresent())
          .forEach(
              e ->
                  valueClasses.put(
                      e.uuid(), (Class<Value>) e.columnScheme().get().getValueClass()));

      ExtPrimaryDataConnection primaryDataConnection = new ExtPrimaryDataConnection(primaryMapping);
      primaryDataConnection.setValueClasses(valueClasses);

      return primaryDataConnection;
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

  // primary data methods

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
    ExtInputDataContainer inputData = dataQueueExtCoSimulatorToSimonaApi.takeData();

    if (inputData.getTick() != tick) {
      throw new RuntimeException(
          String.format(
              "Provided input data for tick %d, but SIMONA expects input data for tick %d",
              inputData.getTick(), tick));
    }

    sendPrimaryDataToSimona(
        extPrimaryDataConnection, tick, inputData.getSimonaInputMap(), maybeNextTick, log);
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

  // energy management data methods


  /**
   * Function to send em flex options from SIMONA to the external simulation using the given {@link
   * ExtEmDataConnection}. This method will provide values to the {@link
   * #dataQueueSimonaApiToExtCoSimulator}.
   *
   * @param extEmDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param maybeNextTick option for the next tick data is sent
   * @param log logger
   * @throws InterruptedException if the fetching of data is interrupted
   */
  protected void sendEmFlexResultsToExt(
      ExtEmDataConnection extEmDataConnection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    // sending flex request to simona
    ExtInputDataContainer container = dataQueueExtCoSimulatorToSimonaApi.takeData();

    Map<String, List<String>> map = container.getSimonaInputMap().entrySet().stream().map(e ->
            Map.entry(e.getKey(), ((FlexOptionRequestValue) e.getValue()).emEntities())
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    log.info("Request flex options for: {}", map);
    Map<String, ResultEntity> results = extEmDataConnection.convertAndSendRequestFlexResults(tick, map, log);

    sendSingleResultType(
        "em flexibility option",
            results,
        tick,
        maybeNextTick,
        log);
  }

  /**
   * Function to send em set points from SIMONA to the external simulation using the given {@link
   * ExtEmDataConnection}. This method will provide values to the {@link
   * #dataQueueSimonaApiToExtCoSimulator}.
   *
   * @param extEmDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param maybeNextTick option for the next tick data is sent
   * @param log logger
   * @throws InterruptedException if the fetching of data is interrupted
   */
  protected void sendEmSetPointsToExt(
      ExtEmDataConnection extEmDataConnection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    sendSingleResultType(
        "em set point",
        extEmDataConnection.requestEmSetPoints(tick, extEmDataConnection.getControlledEms()),
        tick,
        maybeNextTick,
        log);
  }

  /**
   * Function to send em flex options to SIMONA using the given {@link ExtEmDataConnection}. This
   * method will take a value from the {@link #dataQueueExtCoSimulatorToSimonaApi}.
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
  protected void sendEmFlexOptionsToSimona(
      ExtEmDataConnection extEmDataConnection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    ExtInputDataContainer inputData = dataQueueExtCoSimulatorToSimonaApi.takeData();

    if (inputData.getTick() != tick) {
      throw new RuntimeException(
          String.format(
              "Provided input data for tick %d, but SIMONA expects input data for tick %d",
              inputData.getTick(), tick));
    }

    sendEmFlexOptionsToSimona(
        extEmDataConnection, tick, inputData.getSimonaInputMap(), maybeNextTick, log);
  }

  /**
   * Function to send em flex options to SIMONA using the given {@link ExtEmDataConnection}.
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
  protected void sendEmFlexOptionsToSimona(
      ExtEmDataConnection extEmDataConnection,
      long tick,
      Map<String, Value> dataMap,
      Optional<Long> maybeNextTick,
      Logger log) {
    log.debug("Received em flex options from {}", extSimulatorName);
    extEmDataConnection.convertAndSendFlexOptions(tick, dataMap, maybeNextTick, log);
    log.debug("Provided em flex options to SIMONA!");
  }

  /**
   * Function to send em set point data to SIMONA using the given {@link ExtEmDataConnection}. This
   * method will take a value from the {@link #dataQueueExtCoSimulatorToSimonaApi}.
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
    ExtInputDataContainer inputData = dataQueueExtCoSimulatorToSimonaApi.takeData();

    if (inputData.getTick() != tick) {
      throw new RuntimeException(
          String.format(
              "Provided input data for tick %d, but SIMONA expects input data for tick %d",
              inputData.getTick(), tick));
    }

    sendEmSetPointsToSimona(
        extEmDataConnection, tick, inputData.getSimonaInputMap(), maybeNextTick, log);
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
      Map<String, Value> dataMap,
      Optional<Long> maybeNextTick,
      Logger log) {
    log.debug("Received em set points from {}", extSimulatorName);
    extEmDataConnection.convertAndSendSetPoints(tick, dataMap, maybeNextTick, log);
    log.debug("Provided em set points to SIMONA!");
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
    Map<String, ResultEntity> resultsToBeSend = connection.requestResults(tick);
    log.debug("Received results from SIMONA!");
    dataQueueSimonaApiToExtCoSimulator.queueData(
        new ExtResultContainer(tick, resultsToBeSend, maybeNextTick));
    log.debug("Sent results to {}", extSimulatorName);
  }

  private void sendSingleResultType(
      String type,
      Map<String, ResultEntity> resultsToBeSend,
      long tick,
      Optional<Long> nextTick,
      Logger log)
      throws InterruptedException {
    log.info("Request results from SIMONA for {} for tick {}!", type, tick);

    String resultString = resultMapToString(resultsToBeSend);
    log.debug("[{}] Received {} results from SIMONA!\n{}", tick, type, resultString);
    dataQueueSimonaApiToExtCoSimulator.queueData(
        new ExtResultContainer(tick, resultsToBeSend, nextTick));
    log.info("Sent {} results for tick {} to {}", type, tick, extSimulatorName);
  }

  // helper methods

  private String resultMapToString(Map<String, ResultEntity> results) {
    StringBuilder resultString = new StringBuilder();

    for (Map.Entry<String, ResultEntity> entry : results.entrySet()) {
      String key = entry.getKey();
      ResultEntity value = entry.getValue();

      resultString
          .append("id = ")
          .append(key)
          .append(", time = ")
          .append(value.getTime())
          .append(", result = ")
          .append(value.getClass().getSimpleName())
          .append("\n");
    }
    return resultString.toString();
  }
}
