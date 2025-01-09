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
import edu.ie3.simona.api.data.ExtInputDataConnectionWithMapping;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmDataConnection;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultDataConnection;
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
  protected static Set<ExtDataConnection> flat(Optional<? extends ExtDataConnection>... optionals) {
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
   * @return an option for an ext primary data connection
   */
  public static Optional<ExtPrimaryDataConnection> buildPrimaryConnection(
      ExtEntityMapping mapping, Logger log) {
    Map<String, UUID> primaryMapping = mapping.getExtId2UuidMapping(DataType.EXT_PRIMARY_INPUT);

    if (primaryMapping.isEmpty()) {
      log.warn("No primary data connection was created.");
      return Optional.empty();
    } else {
      log.info("Primary data connection with {} entities created.", primaryMapping.size());
      return Optional.of(new ExtPrimaryDataConnection(primaryMapping));
    }
  }

  /**
   * Builds an {@link ExtEmDataConnection}.
   *
   * @param mapping between the external simulation and SIMONA.
   * @param log logger
   * @return an option for an ext em data connection
   */
  public static Optional<ExtEmDataConnection> buildEmConnection(
      ExtEntityMapping mapping, Logger log) {
    Map<String, UUID> emMapping = mapping.getExtId2UuidMapping(DataType.EXT_EM_INPUT);

    if (emMapping.isEmpty()) {
      log.warn("No em data connection was created.");
      return Optional.empty();
    } else {
      log.info("Em data connection with {} entities created.", emMapping.size());
      return Optional.of(new ExtEmDataConnection(emMapping));
    }
  }

  /**
   * Builds an {@link ExtResultDataConnection}.
   *
   * @param mapping between the external simulation and SIMONA.
   * @param log logger
   * @return an option for an ext result data connection
   */
  public static Optional<ExtResultDataConnection> buildResultConnection(
      ExtEntityMapping mapping, Logger log) {
    Map<UUID, String> resultParticipantMapping =
        mapping.getExtUuid2IdMapping(DataType.EXT_PARTICIPANT_RESULT);
    Map<UUID, String> resultGridMapping = mapping.getExtUuid2IdMapping(DataType.EXT_GRID_RESULT);

    if (resultParticipantMapping.isEmpty() && resultGridMapping.isEmpty()) {
      log.warn("No result connection was created.");
      return Optional.empty();
    } else {
      log.info(
          "Result connection with {} participants and {} grid assets created.",
          resultParticipantMapping.size(),
          resultGridMapping.size());
      return Optional.of(new ExtResultDataConnection(resultParticipantMapping, resultGridMapping));
    }
  }

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
      Map<String, Value> dataMap,
      Optional<Long> maybeNextTick,
      Logger log) {
    log.debug("Wait for Primary Data from {}", extSimulatorName);
    log.debug("Received Primary Data from {}", extSimulatorName);
    extPrimaryDataConnection.convertAndSend(tick, dataMap, maybeNextTick, log);
    log.debug("Provided Primary Data to SIMONA!");
  }

  /**
   * Function to send em data to SIMONA using ExtPrimaryData nextTick is necessary, because the em
   * agents have an own scheduler that should know, when the next set point arrives.
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
   * Function to send external data to SIMONA using {@link ExtInputDataConnectionWithMapping}s. This
   * method will automatically take the next {@link ExtInputDataContainer} from the queue.
   *
   * @param dataConnections the connections to SIMONA
   * @param log logger
   */
  protected void sendDataToSIMONA(
      Set<ExtInputDataConnectionWithMapping<?, ?>> dataConnections, Logger log)
      throws InterruptedException {
    sendDataToSIMONA(dataConnections, dataQueueExtCoSimulatorToSimonaApi.takeData(), log);
  }

  /**
   * Function to send external data to SIMONA using {@link ExtInputDataConnectionWithMapping}s.
   *
   * @param dataConnections the connections to SIMONA
   * @param dataContainer contains all necessary inormation
   * @param log logger
   */
  protected void sendDataToSIMONA(
      Set<ExtInputDataConnectionWithMapping<?, ?>> dataConnections,
      ExtInputDataContainer dataContainer,
      Logger log) {
    log.debug("Received data from {}", extSimulatorName);

    Map<String, Value> data = dataContainer.getSimonaInputMap();
    long tick = dataContainer.getTick();
    Optional<Long> maybeNextTick = dataContainer.getMaybeNextTick();

    dataConnections.forEach(con -> con.convertAndSend(tick, data, maybeNextTick, log));

    log.debug("Provided all data from {} to SIMONA", extSimulatorName);
  }

  /**
   * Function to get result data from SIMONA using the available {@link ExtResultDataConnection}
   *
   * @param connection the connection to SIMONA
   * @param tick for which data is received
   * @param maybeNextTick option for the next tick data is received
   * @param log logger
   */
  protected void sendDataToExt(
      ExtResultDataConnection connection, long tick, Optional<Long> maybeNextTick, Logger log)
      throws InterruptedException {
    log.debug("Request results from SIMONA!");
    Map<String, ModelResultEntity> resultsToBeSend = connection.requestResults(tick);
    log.debug("Received results from SIMONA!");
    dataQueueSimonaApiToExtCoSimulator.queueData(
        new ExtResultContainer(tick, resultsToBeSend, maybeNextTick));
    log.debug("Sent results to {}", extSimulatorName);
  }
}
