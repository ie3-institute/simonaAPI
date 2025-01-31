package edu.ie3.simona.api.simpleextsim;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.datamodel.models.result.system.EmResult;
import edu.ie3.datamodel.models.result.system.PvResult;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ExtInputDataContainer;
import edu.ie3.simona.api.data.em.ExtEmData;
import edu.ie3.simona.api.data.em.NoSetPointValue;
import edu.ie3.simona.api.data.results.ExtResultContainer;
import edu.ie3.simona.api.data.results.ExtResultData;
import edu.ie3.simona.api.simulation.ExtSimulation;
import edu.ie3.simona.api.simulation.mapping.ExtEntityEntry;
import edu.ie3.simona.api.simulation.mapping.ExtEntityMapping;
import edu.ie3.simona.api.simulation.mapping.ExtEntityMappingCsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.quantity.Quantities;

import java.nio.file.Path;
import java.util.*;

/**
 * Simple example for an external simulation, that calculates power for two loads, and gets power for two pv plants from SIMONA.
 */

public class SimpleExtSimulationWithEm extends ExtSimulation {

    private final Logger log = LoggerFactory.getLogger(simulationName);

    private final ExtResultData extResultData;
    private final ExtEmData extEmData;

    private final ExtEntityMapping mapping;

    private final UUID em3 = UUID.fromString("fd1a8de9-722a-4304-8799-e1e976d9979c");
    private final UUID em4 = UUID.fromString("ff0b995a-86ff-4f4d-987e-e475a64f2180");

    private final UUID pv3 = UUID.fromString("a1eb7fc1-3bee-4b65-a387-ef3046644bf0");
    private final UUID pv4 = UUID.fromString("9d7cd8e2-d859-4f4f-9c01-abba06ef2e2c");

    private final long deltaT = 900L;

    private final static String PV_3 = "PV_NS_Node_3";
    private final static String PV_4 = "PV_NS_Node_4";

    private final static String EM_3 = "EM_NS_Node_3";
    private final static String EM_4 = "EM_NS_Node_4";

    private final SimpleFlexibilityController emController3 = new SimpleFlexibilityController(
            em3,
            EM_3,
            createTimeSeries3()
    );

    private final SimpleFlexibilityController emController4 = new SimpleFlexibilityController(
            em4,
            EM_4,
            createTimeSeries2()
    );

    private static HashMap<Long, PValue> createTimeSeries1() {
        HashMap<Long, PValue> ts = new HashMap<>();
        ts.put(0L, new PValue(Quantities.getQuantity(-0.5, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(1L, new PValue(Quantities.getQuantity(-0.25, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(2L, new PValue(Quantities.getQuantity(0.25, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(3L, new PValue(Quantities.getQuantity(0.5, StandardUnits.ACTIVE_POWER_IN)));
        return ts;
    }

    private static HashMap<Long, PValue> createTimeSeries3() {
        HashMap<Long, PValue> ts = new HashMap<>();
        ts.put(0L, new PValue(Quantities.getQuantity(-2.5, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(1L, new PValue(Quantities.getQuantity(-2.5, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(2L, new PValue(Quantities.getQuantity(-2.5, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(3L, new PValue(Quantities.getQuantity(-2.5, StandardUnits.ACTIVE_POWER_IN)));
        return ts;
    }

    private static HashMap<Long, PValue> createTimeSeries2() {
        HashMap<Long, PValue> ts = new HashMap<>();
        ts.put(0L, new NoSetPointValue(Quantities.getQuantity(-2.5, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(1L, new NoSetPointValue(Quantities.getQuantity(-2.5, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(2L, new NoSetPointValue(Quantities.getQuantity(-2.5, StandardUnits.ACTIVE_POWER_IN)));
        ts.put(3L, new NoSetPointValue(Quantities.getQuantity(-2.5, StandardUnits.ACTIVE_POWER_IN)));
        return ts;
    }

    public SimpleExtSimulationWithEm(Path mappingPath) {
        super("SimpleExtSimulationWithEm");
        this.mapping = ExtEntityMappingCsvSource.createExtEntityMapping(mappingPath);
        this.extEmData = new ExtEmData(
                mapping.getExtId2UuidMapping(ExtEntityEntry.EXT_INPUT)
        );
        this.extResultData = new ExtResultData(
                mapping.getExtUuid2IdMapping(ExtEntityEntry.EXT_RESULT_PARTICIPANT),
                mapping.getExtUuid2IdMapping(ExtEntityEntry.EXT_RESULT_GRID),
                mapping.getExtUuid2IdMapping(ExtEntityEntry.EXT_RESULT_FLEX_OPTIONS)
        );
    }

    @Override
    protected Long initialize() {
        log.info("Main args handed over to external simulation: {}", Arrays.toString(getMainArgs()));
        return 0L;
    }

    @Override
    protected Optional<Long> doActivity(long tick) {
        log.info("+++++++++++++++++++++++++++ Activities in External simulation: Tick {} has been triggered. +++++++++++++++++++++++++++", tick);

        // Primary Data that should be provided to SIMONA
        Map<String, Value> emDataFromExt = new HashMap<>();

        long phase = (tick / 2000) % 4;

        long nextTick = tick + deltaT;

        emDataFromExt.put(
                EM_3, emController3.getSetPoint(phase)
        );
        emDataFromExt.put(
                EM_4, emController4.getSetPoint(phase)
        );

        ExtInputDataContainer inputData = new ExtInputDataContainer(
                tick,
                emDataFromExt,
                nextTick
        );
        extEmData.provideEmData(
                tick, extEmData.convertExternalInputToEmSetPoints(inputData), Optional.of(nextTick));
        log.info("Provide Primary Data to SIMONA for "
                + EM_3
                + " ("
                + em3
                + ") with "
                + emController3.getSetPoint(phase)
                + " and "
                + EM_4
                + " ("
                + em4
                + ") with "
                + emController4.getSetPoint(phase)
                + ".");

        try {
            log.info("Request results from SIMONA for tick {}!", tick);
            Map<String, ModelResultEntity> resultsToBeSend = extResultData.requestResults(tick);
            ExtResultContainer resultsFromSimona =  new ExtResultContainer(tick, resultsToBeSend, Optional.of(nextTick));
            log.info("Received results from SIMONA!");
            resultsFromSimona.getResults().forEach(
                    (id, result) -> {
                        if (result instanceof PvResult spResult) {
                            if (PV_3.equals(id)) {
                                log.debug("Tick " + tick + ": SIMONA calculated the power of pv3 (" + pv3 + ") with " + spResult);
                                log.info("SIMONA calculated the power of pv3 (" + pv3 + ") with p = " + spResult.getP());
                            } else if (PV_4.equals(id)) {
                                log.debug("Tick " + tick + ": SIMONA calculated the power of pv4 (" + pv4 + ") with " + spResult);
                                log.info("SIMONA calculated the power of pv4 (" + pv4 + ") with p = " + spResult.getP());
                            } else {
                                log.error("Received a result from SIMONA for uuid {}, but I don't expect this entity!", id);
                            }
                        } else if (result instanceof EmResult emResult){
                            if (EM_3.equals(id)) {
                                log.debug("Tick " + tick + ": SIMONA calculated the power of em3 (" + em3 + ") with " + emResult);
                                log.info("SIMONA calculated the power of em3 (" + em3 + ") with p = " + emResult.getP());
                            } else if (EM_4.equals(id)) {
                                log.debug("Tick " + tick + ": SIMONA calculated the power of em4 (" + em4 + ") with " + emResult);
                                log.info("SIMONA calculated the power of em4 (" + em4 + ") with p = " + emResult.getP());
                            } else {
                                log.error("Received a result from SIMONA for uuid {}, but I don't expect this entity!", id);
                            }
                        } else {
                            log.error("Received wrong results from SIMONA!");
                        }
                    }
            );
            log.info("***** External simulation for tick " + tick + " completed. Next simulation tick = " + nextTick + " *****");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(nextTick);
    }

    @Override
    public List<ExtData> getDataConnections() {
        return List.of(
                extEmData,
                extResultData
        );
    }

}