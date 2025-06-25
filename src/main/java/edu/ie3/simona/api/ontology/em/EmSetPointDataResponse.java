/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.EmSetPointResult;
import java.util.Map;
import java.util.UUID;

/** Message that provides em data (set points) to an external simulation. */
public record EmSetPointDataResponse(Map<UUID, EmSetPointResult> emData)
    implements EmDataResponseMessageToExt {}
