/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation;

import ch.qos.logback.classic.Logger;
import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.simona.api.data.DataQueueExtSimulationExtSimulator;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmData;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryData;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultData;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract class for an external co-simulation with the structure: external api - ext-co-simulation
 * - extsimulation - simonaAPI - simona It contains all function to transfer primary data and em
 * data to SIMONA and results to the external co-simulation.
 */
public abstract class ExtCoSimulation extends ExtSimulation {

  /** Queue for the data connection from the external co-simulation to SimonaAPI */
  protected final DataQueueExtSimulationExtSimulator<ExtInputDataContainer>
      dataQueueExtCoSimulatorToSimonaApi;

  /** Queue for the data connection from SimonaAPI to the external co-simulation */
  protected final DataQueueExtSimulationExtSimulator<ExtResultContainer>
      dataQueueSimonaApiToExtCoSimulator;

  /** Time resolution for the data exchange */
  private final long deltaT = 900L;

  /** Name of the external co-simulation */
  protected final String extSimulatorName;

  protected ExtCoSimulation(String simulationName, String extSimulatorName) {
    super(simulationName);
    this.extSimulatorName = extSimulatorName;
    this.dataQueueExtCoSimulatorToSimonaApi = new DataQueueExtSimulationExtSimulator<>();
    this.dataQueueSimonaApiToExtCoSimulator = new DataQueueExtSimulationExtSimulator<>();
  }

  /** Function to send primary data to SIMONA using ExtPrimaryData */
  protected void sendPrimaryDataToSimona(ExtPrimaryData extPrimaryData, long tick, Logger log)
      throws InterruptedException {
    log.debug("Wait for Primary Data from " + extSimulatorName);
    ExtInputDataContainer inputData = dataQueueExtCoSimulatorToSimonaApi.takeData();
    log.debug("Received Primary Data from " + extSimulatorName);
    extPrimaryData.providePrimaryData(
        tick,
        extPrimaryData.convertExternalInputToPrimaryData(inputData),
        inputData.getMaybeNextTick());
    log.debug("Provided Primary Data to SIMONA!");
  }

  /**
   * Function to send em data to SIMONA using ExtPrimaryData nextTick is necessary, because the em
   * agents have an own scheduler that should know, when the next set point arrives.
   */
  protected void sendEmDataToSimona(ExtEmData extEmData, long tick, long nextTick, Logger log)
      throws InterruptedException {
    log.debug("Wait for EmData from " + extSimulatorName);
    ExtInputDataContainer inputData = dataQueueExtCoSimulatorToSimonaApi.takeData();
    log.debug("Received EmData from " + extSimulatorName);
    extEmData.provideEmData(
        tick, extEmData.convertExternalInputToEmSetPoints(inputData), Optional.of(nextTick));
    log.debug("Provided EmData to SIMONA!");
  }

  /** Function to get result data from SIMONA using ExtResultData */
  protected void sendResultsToExtCoSimulator(
      ExtResultData extResultData, long tick, Optional<Long> nextTick, Logger log)
      throws InterruptedException {
    log.debug("Request results from SIMONA!");
    Map<String, ModelResultEntity> resultsToBeSend = extResultData.requestResults(tick);
    log.debug("Received results from SIMONA!");
    dataQueueSimonaApiToExtCoSimulator.queueData(
        new ExtResultContainer(tick, resultsToBeSend, nextTick));
    log.debug("Sent results to " + extSimulatorName);
  }
}
