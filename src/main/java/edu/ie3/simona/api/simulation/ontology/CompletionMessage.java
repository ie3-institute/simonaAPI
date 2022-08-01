/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.ontology;

import java.util.Collection;

public record CompletionMessage(Collection<Long> newTriggers)
    implements FromExtSimControlResponseMessage {}
