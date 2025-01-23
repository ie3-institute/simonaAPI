package edu.ie3.simona.api.data.ontology;

import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Power;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DesaggFlexOptionsResult extends FlexOptionsResult {

    private final Map<String, FlexOptionsResult> connectedFlexOptionResults;

    public DesaggFlexOptionsResult(ZonedDateTime time, UUID inputModel, ComparableQuantity<Power> pRef, ComparableQuantity<Power> pMin, ComparableQuantity<Power> pMax, Map<String, FlexOptionsResult> connectedFlexOptionResults) {
        super(time, inputModel, pRef, pMin, pMax);
        this.connectedFlexOptionResults = connectedFlexOptionResults;
    }

    public DesaggFlexOptionsResult(FlexOptionsResult flexOptionsResult, Map<String, FlexOptionsResult> connectedFlexOptionResults) {
        super(flexOptionsResult.getTime(), flexOptionsResult.getInputModel(), flexOptionsResult.getpRef(), flexOptionsResult.getpMin(), flexOptionsResult.getpMax());
        this.connectedFlexOptionResults = connectedFlexOptionResults;
    }

    public DesaggFlexOptionsResult(FlexOptionsResult flexOptionsResult) {
        super(flexOptionsResult.getTime(), flexOptionsResult.getInputModel(), flexOptionsResult.getpRef(), flexOptionsResult.getpMin(), flexOptionsResult.getpMax());
        this.connectedFlexOptionResults = Collections.emptyMap();
    }

    public Map<String, FlexOptionsResult> getConnectedFlexOptionResults() {
        return connectedFlexOptionResults;
    }
}
