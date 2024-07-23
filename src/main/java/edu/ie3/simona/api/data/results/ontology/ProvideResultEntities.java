/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results.ontology;

import edu.ie3.datamodel.models.result.ModelResultEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Provides a list of results from SIMONA to an external simulation. */
public record ProvideResultEntities(List<ModelResultEntity> results) implements ResultDataResponseMessageToExt {
    public ProvideResultEntities() {
        this(new ArrayList<>());
    }

    public ProvideResultEntities(Map<UUID, ModelResultEntity> resultMap) {
        this(resultMap.values().stream().toList());
    }

    public ProvideResultEntities(List<ModelResultEntity> results) {
        this.results = results;
    }
}
