package edu.ie3.simona.api.data.results.ontology;

import java.util.List;
import java.util.UUID;

public record ProvideExpectedAssets(List<UUID> expectedAssets) implements ResultDataMessageFromExt {}
