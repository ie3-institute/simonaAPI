package edu.ie3.simona.api.data.primarydata.ontology;

import edu.ie3.datamodel.models.value.Value;
import java.util.Map;
import java.util.UUID;

public record ProvidePrimaryData(long tick, Map<UUID, Value> primaryData) implements PrimaryDataMessageFromExt {}