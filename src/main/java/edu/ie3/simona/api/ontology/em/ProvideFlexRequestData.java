/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Message that provides em data (flex requests) from an external simulation. */
public record ProvideFlexRequestData(
    long tick, Map<UUID, List<UUID>> flexRequests, Optional<Long> maybeNextTick)
    implements EmDataMessageFromExt {}
