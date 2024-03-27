/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.ontology;

/** Interface for control messages from the external simulation to SIMONA */
public interface ControlResponseMessageFromExt {
    int getPhase();
}
