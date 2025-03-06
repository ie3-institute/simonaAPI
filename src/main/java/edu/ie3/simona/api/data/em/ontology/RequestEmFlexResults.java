/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.ontology;

import java.util.List;
import java.util.UUID;

/** Request em set points from SIMONA in the given tick. */
public record RequestEmFlexResults(long tick, List<UUID> emEntities)
    implements EmDataMessageFromExt {}
