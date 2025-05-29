/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.result;

import java.util.List;
import java.util.UUID;

/** Request calculated results from SIMONA in the current tick */
public record RequestResultEntities(long tick, List<UUID> requestedResults)
    implements ResultDataMessageFromExt {}
