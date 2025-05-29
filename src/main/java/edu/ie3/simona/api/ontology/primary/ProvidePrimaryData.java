/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.primary;

import edu.ie3.datamodel.models.value.Value;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Message that provides primary data from an external primary data simulation */
public record ProvidePrimaryData(
    long tick, Map<UUID, Value> primaryData, Optional<Long> maybeNextTick)
    implements PrimaryDataMessageFromExt {}
