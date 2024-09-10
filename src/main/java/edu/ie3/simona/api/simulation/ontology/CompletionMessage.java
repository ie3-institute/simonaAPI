/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.ontology;

import java.util.Optional;

/**
 * Message that is returned to SIMONA
 *
 * @param nextActivation The tick that the external simulation would like to be activated at again
 * @param phase The phase of the simulation
 */
public record CompletionMessage(Optional<Long> nextActivation)
    implements ControlResponseMessageFromExt {}
