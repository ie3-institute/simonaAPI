package edu.ie3.simona.api;

import edu.ie3.simona.api.exceptions.NoExtSimulationException;
import edu.ie3.simona.api.simulation.ExtSimulation;

/**
 * This interface needs to be implemented to provide SIMONA with an external simulation.
 */
public non-sealed interface ExtSimulationProvider extends ExtLinkInterface {

    /** Returns the external simulation. */
    default ExtSimulation getExtSimulation() {
        throw new NoExtSimulationException(this.getClass());
    }

}
