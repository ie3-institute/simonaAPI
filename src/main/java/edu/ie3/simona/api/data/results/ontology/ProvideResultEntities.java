package edu.ie3.simona.api.data.results.ontology;

import edu.ie3.datamodel.models.result.ResultEntity;

import java.util.List;

public record ProvideResultEntities(
        List<ResultEntity> resultEntityMap
) implements ResultDataMessageToExt {
}
