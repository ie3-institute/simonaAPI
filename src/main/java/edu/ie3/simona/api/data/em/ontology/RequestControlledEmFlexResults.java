package edu.ie3.simona.api.data.em.ontology;

import java.util.Set;
import java.util.UUID;

/** Request em set flex options from SIMONA via external simulation. */
public record RequestControlledEmFlexResults(Set<UUID> emEntities) implements EmDataResponseMessageToExt { }
