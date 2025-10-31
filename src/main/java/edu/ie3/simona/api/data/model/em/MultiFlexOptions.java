package edu.ie3.simona.api.data.model.em;

import java.util.List;
import java.util.UUID;


public record MultiFlexOptions(UUID receiver, List<FlexOptions> flexOptions) implements EmData {
}
