package edu.ie3.simona.api.data.dcopf.ontology;

import java.util.*;

public class SetPointsMessage implements ExtOpfMessage {

    private final Map<UUID, Double> setpoints;

    public SetPointsMessage(Map<UUID, Double> setpoints) {this.setpoints = setpoints;}

    public Map<UUID, Double> getSetpoints() {return setpoints;}

    //zu klären: woher kommen die UUIDs der generatoren und woher die setpoints?
    //--> Verbindung zum Interface

}
