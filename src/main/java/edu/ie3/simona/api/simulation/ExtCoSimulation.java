/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.DataQueueExtSimulationExtSimulator;
import edu.ie3.simona.api.data.ExtInputDataConnection;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmDataConnection;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultDataConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

  @Override
  protected Optional<Long> doActivity(long tick) {
    log.info(
            "+++++++++++++++++++++++++++ Activities in External simulation: Tick {} has been triggered. +++++++++++++++++++++++++++",
            tick);
    try {
      Thread.sleep(500);

      Optional<Long> nextTick = Optional.of(tick + deltaT);
      sendDataToSimona(tick, nextTick);
      sendDataToExt(tick, nextTick);

      return nextTick;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *
   * Returns a set of all the {@link ExtInputDataConnection}s.
   */
  protected abstract Set<ExtInputDataConnection> getInputDataConnections();

  /**
   *
   * Returns an option for an {@link ExtResultDataConnection}.
   */
  protected abstract  Optional<ExtResultDataConnection> getResultDataConnection();

  /**
   * Function to send external data to SIMONA using the available {@link ExtInputDataConnection}s.
   */
  protected void sendDataToSimona(long tick, Optional<Long> maybeNextTick) throws InterruptedException {
    log.debug("Wait for external data from {}", extSimulatorName);
    ExtInputDataContainer inputData = dataQueueExtCoSimulatorToSimonaApi.takeData();
    log.debug("Received external data from {}", extSimulatorName);

    Map<String, Value> data = inputData.getSimonaInputMap();
    Set<ExtInputDataConnection> extInputDataConnections = getInputDataConnections();

    if (!extInputDataConnections.isEmpty()) {
      extInputDataConnections.forEach(inputDataConnection -> {
        if (inputDataConnection instanceof ExtPrimaryDataConnection primary) {
          primary.convertAndSend(tick, data, maybeNextTick, log);
        } else if (inputDataConnection instanceof ExtEmDataConnection em) {
          em.convertAndSend(tick, data, maybeNextTick, log);
        }
      });
      log.debug("Provided SIMONA with external data!");
    } else {
      log.info("No connections found. Sending no external data to SIMONA!");
    }
  }

  /** Function to get result data from SIMONA using the available {@link ExtResultDataConnection} */
  protected void sendDataToExt(long tick, Optional<Long> nextTick) throws InterruptedException {
    log.debug("Request results from SIMONA!");

    Optional<ExtResultDataConnection> resultDataConnection = getResultDataConnection();

    if (resultDataConnection.isPresent()) {
      ExtResultDataConnection connection = resultDataConnection.get();

      Map<String, ModelResultEntity> resultsToBeSend = connection.requestResults(tick);
      log.debug("Received results from SIMONA!");
      dataQueueSimonaApiToExtCoSimulator.queueData(new ExtResultContainer(tick, resultsToBeSend, nextTick));
      log.debug("Sent results to {}", extSimulatorName);
    } else {
      log.info("No connection found. Sending no results to {}!", extSimulatorName);
    }
  }
}
