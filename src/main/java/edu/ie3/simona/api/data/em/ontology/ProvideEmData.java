/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.ontology;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.primarydata.ontology.PrimaryDataMessageFromExt;

import java.util.Map;
import java.util.UUID;

/** Message that provides primary data from an external primary data simulation */
public record ProvideEmData(
        long tick,
        Map<UUID, PValue> emData) implements EmDataMessageFromExt {}
