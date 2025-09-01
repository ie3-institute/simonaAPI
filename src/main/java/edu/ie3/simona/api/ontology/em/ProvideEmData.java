package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.EmSetPoint;
import edu.ie3.simona.api.data.model.em.FlexOptionRequest;
import edu.ie3.simona.api.data.model.em.FlexOptions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Message that provides em data from an external simulation. */
public record ProvideEmData(
        long tick,
        Map<UUID, FlexOptionRequest> flexRequests,
        Map<UUID, List<FlexOptions>> flexOptions,
        Map<UUID, EmSetPoint> setPoints,
        Optional<Long> maybeNextTick
) implements EmDataMessageFromExt  {}
