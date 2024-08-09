/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results.ontology;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import java.util.List;

/** Provides a list of results from SIMONA to an external simulation. */
public record ProvideResultEntities(List<ModelResultEntity> results)
    implements ResultDataResponseMessageToExt {}
