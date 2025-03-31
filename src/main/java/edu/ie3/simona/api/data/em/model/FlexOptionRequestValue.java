/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.value.Value;
import java.util.List;
import java.util.UUID;

public record FlexOptionRequestValue(UUID requester, List<UUID> emEntities) implements Value {}
