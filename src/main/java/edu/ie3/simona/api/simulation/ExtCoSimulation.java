package edu.ie3.simona.api.simulation;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.simona.api.data.DataQueueExtSimulationExtSimulator;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmDataConnection;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryDataConnection;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultDataConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Abstract class for an external co-simulation with the structure: external api - ext-co-simulation - extsimulation -  simonaAPI - simona
 * It contains all function to transfer primary data and em data to SIMONA and results to the external co-simulation.
 */
public abstract class ExtCoSimulation extends ExtSimulation {

    protected final Logger log = LoggerFactory.getLogger(simulationName);

    protected final DataQueueExtSimulationExtSimulator<ExtInputDataContainer> dataQueueExtCoSimulatorToSimonaApi;
    protected final DataQueueExtSimulationExtSimulator<ExtResultContainer> dataQueueSimonaApiToExtCoSimulator;

    protected final long deltaT = 900L;

    protected ExtCoSimulation(String simulationName) {
        super(simulationName);
        this.dataQueueExtCoSimulatorToSimonaApi = new DataQueueExtSimulationExtSimulator<>();
        this.dataQueueSimonaApiToExtCoSimulator = new DataQueueExtSimulationExtSimulator<>();
    }

    protected void sendPrimaryDataToSimona(
            ExtPrimaryDataConnection extPrimaryData,
            long tick
    ) {
        try {
            ExtInputDataContainer inputData = dataQueueExtCoSimulatorToSimonaApi.takeData();
            log.debug("Received Primary Data from {} = {}", simulationName, inputData);

            extPrimaryData.providePrimaryData(
                    tick,
                    extPrimaryData.convertExternalInputToPrimaryData(inputData),
                    inputData.getMaybeNextTick()
            );
            log.info("Provided Primary Data to SIMONA");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendEmDataToSimona(
            ExtEmDataConnection extEmData,
            long tick,
            long nextTick
    ) {
        try {
            log.info("+++++ External simulation triggered for tick {} +++++", tick);
            log.info("Wait for new EmData from OpSim...");
            ExtInputDataContainer rawEmData = dataQueueExtCoSimulatorToSimonaApi.takeData();
            extEmData.provideEmData(
                    tick,
                    extEmData.createExtEmDataMap(rawEmData),
                    Optional.of(nextTick)
            );
            log.info("Provided EmData to SIMONA!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendResultsToExtCoSimulator(
            ExtResultDataConnection extResultData,
            long tick,
            long nextTick
    ) {
        try {
            log.info("Request Results from SIMONA!");
            Map<String, ModelResultEntity> resultsToBeSend = extResultData.requestResults(tick);
            log.info("Received results from SIMONA! Now convert them and send them to Mosaik!");

            dataQueueSimonaApiToExtCoSimulator.queueData(new ExtResultContainer(tick, resultsToBeSend));
            log.info("***** External simulation for tick {} completed. Next simulation tick = {} *****", tick, nextTick);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
