package edu.ie3.simona.api.simulation.mapping;

import java.util.*;

public class ExtEntityMapping {

    private final List<ExtEntityEntry> extEntities;

    public ExtEntityMapping(
            List<ExtEntityEntry> extEntityEntryList
    ) {
        this.extEntities = extEntityEntryList;
    }

    public Map<String, UUID> getExtIdUuidMapping(
            String dataType
    ) {
        Map<String, UUID> extIdUuidMapping = new HashMap<>();
        extEntities.forEach(
                ent -> {
                    if (Objects.equals(ent.resultType(), dataType)) {
                        extIdUuidMapping.put(
                                ent.id(), ent.uuid()
                        );
                    }
                }
        );
        return extIdUuidMapping;
    }

    public Map<UUID, String> getExtUuidIdMapping(
            String dataType
    ) {
        Map<UUID, String> extUuidIdMapping = new HashMap<>();
        extEntities.forEach(
                ent -> {
                    if (Objects.equals(ent.resultType(), dataType)) {
                        extUuidIdMapping.put(ent.uuid(), ent.id());
                    }
                }
        );
        return extUuidIdMapping;
    }
}
