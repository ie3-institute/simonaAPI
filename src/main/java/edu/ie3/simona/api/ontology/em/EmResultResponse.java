package edu.ie3.simona.api.ontology.em;

import edu.ie3.datamodel.models.result.ResultEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EmResultResponse(Map<UUID, List<ResultEntity>> emResults) implements EmDataResponseMessageToExt {}
