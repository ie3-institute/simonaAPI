package edu.ie3.simona.api.data.dcopf.ontology.builder;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.dcopf.ontology.SetpointsMessage;
import edu.ie3.simona.api.data.ev.ontology.EvMovementsMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetpointsMessageBuilder {

    public SetpointsMessage build() {
        final Map<UUID, PValue> setpoints = new HashMap<>();

        //hier müssen setpoints gefüllt werden
        return new SetpointsMessage(setpoints);
    }
}
