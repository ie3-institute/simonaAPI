/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import java.util.Optional;

/**
 * Request send to SIMONA to finish the em service for the given tick.
 *
 * @param tick for which the em service should be finished
 * @param maybeNextTick option for the next tick
 */
public record RequestEmCompletion(long tick, Optional<Long> maybeNextTick) implements EmDataMessageFromExt {}
