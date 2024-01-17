package edu.ie3.simona.api.data.primarydata.ontology;

import java.util.List;
import java.util.UUID;

public record ProvideSimulatedAssets(List<UUID> coveredAgents) implements PrimaryDataMessageFromExt {}
