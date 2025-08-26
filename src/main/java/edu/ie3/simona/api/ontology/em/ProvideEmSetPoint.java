/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.EmSetPoint;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Message that provides em data (set points) from an external simulation. */
public record ProvideEmSetPoint(
    long tick, Map<UUID, EmSetPoint> emSetPoints, Optional<Long> maybeNextTick)
    implements EmDataMessageFromExt {}
