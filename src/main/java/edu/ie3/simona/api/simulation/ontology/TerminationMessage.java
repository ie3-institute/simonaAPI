/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.ontology;

/**
 * Message that is sent once SIMONA is terminating, indicating that the external simulation should
 * shut down as well
 *
 * @param simulationSuccessful Whether SIMONA terminated successfully
 */
public record TerminationMessage(Boolean simulationSuccessful, int phase) implements ControlMessageToExt {
    @Override
    public int getPhase() {
        return phase;
    }
}
