package edu.ie3.simona.api.data.em.ontology;

import edu.ie3.simona.api.data.em.model.FlexOptionRequestValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Message that provides em data (flex requests) from an external simulation. */
public record ProvideFlexRequestData(
        long tick, Map<UUID, List<UUID>> flexRequests, Optional<Long> maybeNextTick)
        implements EmDataMessageFromExt {}