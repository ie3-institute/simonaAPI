/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import java.util.Set;
import java.util.UUID;

/** Request em set flex options from SIMONA via external simulation. */
public record RequestControlledEmFlexResults(Set<UUID> emEntities)
    implements EmDataResponseMessageToExt {}
