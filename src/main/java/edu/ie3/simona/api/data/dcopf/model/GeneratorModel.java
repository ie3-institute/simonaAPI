package edu.ie3.simona.api.data.dcopf.model;

import edu.ie3.simona.api.data.ev.model.EvModel;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import java.util.UUID;

public interface GeneratorModel {
    /** @return the uuid of this generator (SIMONA)*/
    UUID getUuid();

    /** @return the id of this generator (MATPOWER)*/
    String getId();

    /** @return the current active power setpoint */
    ComparableQuantity<Power> getSetpoint();

    /**
     * @param newSetpoint the new stored energy
     * @return a copy of this ev model with given new stored energy
     */
    GeneratorModel copyWith(ComparableQuantity<Power> newSetpoint);
}
