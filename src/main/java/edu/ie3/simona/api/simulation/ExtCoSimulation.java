package edu.ie3.simona.api.simulation;

import ch.qos.logback.classic.Logger;
import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.DataQueueExtSimulationExtSimulator;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmData;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryData;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultData;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Abstract class for an external co-simulation with the structure: external api - ext-co-simulation - extsimulation -  simonaAPI - simona
 * It contains all function to transfer primary data and em data to SIMONA and results to the external co-simulation.
 */
public abstract class ExtCoSimulation extends ExtSimulation {

    protected final ch.qos.logback.classic.Logger log = (Logger) LoggerFactory.getLogger(simulationName);

    protected final DataQueueExtSimulationExtSimulator<ExtInputDataContainer> dataQueueExtCoSimulatorToSimonaApi;
    protected final DataQueueExtSimulationExtSimulator<ExtResultContainer> dataQueueSimonaApiToExtCoSimulator;

    private final long deltaT = 900L;

    protected ExtCoSimulation(String simulationName) {
        super(simulationName);
        this.dataQueueExtCoSimulatorToSimonaApi = new DataQueueExtSimulationExtSimulator<>();
        this.dataQueueSimonaApiToExtCoSimulator = new DataQueueExtSimulationExtSimulator<>();
    }

    protected void sendPrimaryDataToSimona(
            ExtPrimaryData extPrimaryData,
            long tick
    ) {
        try {
            ExtInputDataContainer inputData = dataQueueExtCoSimulatorToSimonaApi.takeData();
            log.debug("Received Primary Data from " + simulationName + " = " + inputData);

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
            ExtEmData extEmData,
            long tick,
            long nextTick
    ) {
        try {
            log.info("+++++ External simulation triggered for tick " + tick + " +++++");
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
            ExtResultData extResultData,
            long tick,
            long nextTick
    ) {
        try {
            log.info("Request Results from SIMONA!");
            Map<String, ModelResultEntity> resultsToBeSend = extResultData.requestResults(tick);
            log.info("Received results from SIMONA! Now convert them and send them to Mosaik!");

            dataQueueSimonaApiToExtCoSimulator.queueData(new ExtResultContainer(tick, resultsToBeSend));
            log.info("***** External simulation for tick " + tick + " completed. Next simulation tick = " + nextTick + " *****");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
