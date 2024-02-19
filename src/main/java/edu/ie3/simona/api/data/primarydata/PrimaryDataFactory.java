package edu.ie3.simona.api.data.primarydata;

import edu.ie3.datamodel.models.value.Value;

/**
 * Interface that should be implemented by an external simulation.
 */
public abstract class PrimaryDataFactory {

    /** Should convert an object to an primary data value with a check if the object is primary data */
    public abstract Value convertObjectToValue(Object entity) throws Exception;

    /**
     * Valid primary data
     *   ACTIVE_POWER("p", PValue.class),
     *   APPARENT_POWER("pq", SValue.class),
     *   HEAT_DEMAND("h", HeatDemandValue.class),
     *   ACTIVE_POWER_AND_HEAT_DEMAND("ph", HeatAndPValue.class),
     *   APPARENT_POWER_AND_HEAT_DEMAND("pqh", HeatAndSValue.class),
     */
}
