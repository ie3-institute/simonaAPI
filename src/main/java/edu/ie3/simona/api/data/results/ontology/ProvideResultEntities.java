/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results.ontology;

import edu.ie3.datamodel.models.result.ResultEntity;

import java.util.ArrayList;
import java.util.List;

/** Provides a list of results from SIMONA to an external simulation. */
public record ProvideResultEntities(List<ResultEntity> results) implements ResultDataResponseMessageToExt {
    public ProvideResultEntities() {
        this(new ArrayList<>());
    }

    public ProvideResultEntities(List<ResultEntity> results) {
        this.results = results;
    }


}
