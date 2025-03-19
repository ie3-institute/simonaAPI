package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.value.Value;

import java.util.List;

public record FlexOptionRequestValue(
        String requester, List<String> emEntities
) implements Value {}
