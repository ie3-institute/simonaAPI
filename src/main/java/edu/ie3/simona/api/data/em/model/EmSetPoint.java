package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.SValue;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import java.util.Optional;
import java.util.UUID;

public record EmSetPoint(UUID receiver, Optional<ComparableQuantity<Power>> p, Optional<ComparableQuantity<Power>> q, Optional<ComparableQuantity<Time>> delay) {
    
    public static EmSetPoint empty(UUID receiver) {
        return new EmSetPoint(receiver, Optional.empty(), Optional.empty(), Optional.empty());
    }
    
    public static EmSetPoint from(UUID receiver, PValue pValue) {
        if (pValue instanceof SValue s) {
            return new EmSetPoint(receiver, s.getP(), s.getQ(), Optional.empty());
        } else {
            return new EmSetPoint(receiver, pValue.getP(), Optional.empty(), Optional.empty());
        }
    }

    public static EmSetPoint from(UUID receiver, PValue pValue, Optional<ComparableQuantity<Time>> delay) {
        if (pValue instanceof SValue s) {
            return new EmSetPoint(receiver, s.getP(), s.getQ(), delay);
        } else {
            return new EmSetPoint(receiver, pValue.getP(), Optional.empty(), delay);
        }
    }
    
}
