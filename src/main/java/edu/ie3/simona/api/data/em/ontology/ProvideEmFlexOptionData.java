/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.ontology;

import edu.ie3.simona.api.data.em.model.FlexOptions;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Message that provides em data (flexibility options) from an external simulation. */
public record ProvideEmFlexOptionData(
    long tick, Map<UUID, Map<UUID, FlexOptions>> flexOptions, Optional<Long> maybeNextTick)
    implements EmDataMessageFromExt {}
