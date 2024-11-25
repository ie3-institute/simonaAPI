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

  /** Function to send primary data to SIMONA using ExtPrimaryData */
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

  /** Function to get result data from SIMONA using the available {@link ExtResultDataConnection} */
  protected void sendDataToExt(
      ExtResultDataConnection connection, long tick, Optional<Long> nextTick)
      throws InterruptedException {
    log.debug("Request results from SIMONA!");
    Map<String, ModelResultEntity> resultsToBeSend = connection.requestResults(tick);
    log.debug("Received results from SIMONA!");
    dataQueueSimonaApiToExtCoSimulator.queueData(
        new ExtResultContainer(tick, resultsToBeSend, nextTick));
    log.debug("Sent results to {}", extSimulatorName);
  }
}
