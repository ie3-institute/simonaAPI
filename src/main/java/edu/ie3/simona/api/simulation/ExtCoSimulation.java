/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.DataQueueExtSimulationExtSimulator;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmDataConnection;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultDataConnection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import edu.ie3.simona.api.simulation.mapping.DataType;
import edu.ie3.simona.api.simulation.mapping.ExtEntityMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for an external co-simulation with the structure: external api - ext-co-simulation
 * - extsimulation - simonaAPI - simona It contains all function to transfer primary data and em
 * data to SIMONA and results to the external co-simulation.
 */
public abstract class ExtCoSimulation extends ExtSimulation {

  protected Logger log;

  /** Queue for the data connection from the external co-simulation to SimonaAPI */
  protected final DataQueueExtSimulationExtSimulator<ExtInputDataContainer>
      dataQueueExtCoSimulatorToSimonaApi;

  /** Queue for the data connection from SimonaAPI to the external co-simulation */
  protected final DataQueueExtSimulationExtSimulator<ExtResultContainer>
      dataQueueSimonaApiToExtCoSimulator;

  /** Time resolution for the data exchange */
  protected final long deltaT = 900L;

  /** Name of the external co-simulation */
  protected final String extSimulatorName;

  protected ExtCoSimulation(String simulationName, String extSimulatorName) {
    super(simulationName);
    this.extSimulatorName = extSimulatorName;
    log = LoggerFactory.getLogger(simulationName);

    this.dataQueueExtCoSimulatorToSimonaApi = new DataQueueExtSimulationExtSimulator<>();
    this.dataQueueSimonaApiToExtCoSimulator = new DataQueueExtSimulationExtSimulator<>();
  }

  /**
   * Builds an {@link ExtPrimaryDataConnection}.
   * @param mapping between the external simulation and SIMONA.
   * @return an ext primary data connection
   */
  protected ExtPrimaryDataConnection buildPrimaryConnection(ExtEntityMapping mapping) {
    Map<String, UUID> primaryMapping = mapping.getExtId2UuidMapping(DataType.EXT_PRIMARY_INPUT);
    ExtPrimaryDataConnection extPrimaryDataConnection = new ExtPrimaryDataConnection(primaryMapping);

    if (primaryMapping.isEmpty()) {
      log.warn("Primary with 0 entities created.");
    } else {
      log.info("Primary connection with {} entities created.", primaryMapping.size());
    }

    return extPrimaryDataConnection;
  }

  /**
   * Builds an {@link ExtEmDataConnection}.
   * @param mapping between the external simulation and SIMONA.
   * @return an ext em data connection
   */
  protected ExtEmDataConnection buildEmConnection(ExtEntityMapping mapping) {
    Map<String, UUID> emMapping = mapping.getExtId2UuidMapping(DataType.EXT_EM_INPUT);
    ExtEmDataConnection extEmDataConnection = new ExtEmDataConnection(emMapping);

    if (emMapping.isEmpty()) {
      log.warn("Em connection with 0 entities created.");
    } else {
      log.info("Em connection with {} entities created.", emMapping.size());
    }

    return extEmDataConnection;
  }

  /**
   * Builds an {@link ExtResultDataConnection}.
   * @param mapping between the external simulation and SIMONA.
   * @return an ext result data connection
   */
  protected ExtResultDataConnection buildResultConnection(ExtEntityMapping mapping) {
    Map<UUID, String> resultParticipantMapping = mapping.getExtUuid2IdMapping(DataType.EXT_RESULT_PARTICIPANT);
    Map<UUID, String> resultGridMapping = mapping.getExtUuid2IdMapping(DataType.EXT_RESULT_GRID);
    ExtResultDataConnection extResultDataConnection = new ExtResultDataConnection(resultParticipantMapping, resultGridMapping);

    if (resultParticipantMapping.isEmpty() && resultGridMapping.isEmpty()) {
      log.warn("Result connection with 0 participants and 0 grid assets created.");
    } else {
      log.info("Result connection with {} participants and {} grid assets created.", resultParticipantMapping.size(), resultGridMapping.size());
    }

    return extResultDataConnection;
  }


  /** Function to send primary data to SIMONA using ExtPrimaryData
   *
   * @param extPrimaryDataConnection the connection to SIMONA
   * @param tick for which data is sent
   * @param dataMap map: id to value
   * @param maybeNextTick option for the next tick data is sent
   */
  protected void sendPrimaryDataToSimona(
      ExtPrimaryDataConnection extPrimaryDataConnection,
      long tick,
      Map<String, Value> dataMap,
      Optional<Long> maybeNextTick) {
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
   */
  protected void sendEmDataToSimona(
      ExtEmDataConnection extEmDataConnection,
      long tick,
      Map<String, Value> dataMap,
      Optional<Long> maybeNextTick) {
    log.debug("Wait for EmData from {}", extSimulatorName);
    log.debug("Received EmData from {}", extSimulatorName);
    extEmDataConnection.convertAndSend(tick, dataMap, maybeNextTick, log);
    log.debug("Provided EmData to SIMONA!");
  }

  /** Function to get result data from SIMONA using the available {@link ExtResultDataConnection}
   *
   * @param connection the connection to SIMONA
   * @param tick for which data is received
   * @param maybeNextTick option for the next tick data is received
   */
  protected void sendDataToExt(
      ExtResultDataConnection connection, long tick, Optional<Long> maybeNextTick)
      throws InterruptedException {
    log.debug("Request results from SIMONA!");
    Map<String, ModelResultEntity> resultsToBeSend = connection.requestResults(tick);
    log.debug("Received results from SIMONA!");
    dataQueueSimonaApiToExtCoSimulator.queueData(
        new ExtResultContainer(tick, resultsToBeSend, maybeNextTick));
    log.debug("Sent results to {}", extSimulatorName);
  }
}
