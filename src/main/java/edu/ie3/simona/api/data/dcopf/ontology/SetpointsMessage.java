package edu.ie3.simona.api.data.dcopf.ontology;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.ev.model.EvModel;

import java.util.*;

public class SetpointsMessage implements ExtOpfMessage {

    private final Map<UUID, PValue> setpoints;

    public SetpointsMessage(Map<UUID, PValue> setpoints) {this.setpoints = setpoints;}

    public Map<UUID, PValue> getSetpoints() {return setpoints;}


    //wie werden die setpoints gefüllt? Ich erkenne keine Ähnlichkeit zur EV, die ich nutzen könnte

}
