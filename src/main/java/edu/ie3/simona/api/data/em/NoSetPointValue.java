package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Power;

public class NoSetPointValue extends PValue {
    public NoSetPointValue(ComparableQuantity<Power> p) {
        super(p);
    }
}
