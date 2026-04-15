/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import java.util.OptionalLong;

/**
 * Request send to SIMONA to finish the em service for the given tick.
 *
 * @param tick for which the em service should be finished
 * @param maybeNextTick option for the next tick
 */
public record RequestEmCompletion(long tick, OptionalLong maybeNextTick)
    implements EmDataMessageFromExt {
  public RequestEmCompletion(long tick) {
    this(tick, OptionalLong.empty());
  }
}
