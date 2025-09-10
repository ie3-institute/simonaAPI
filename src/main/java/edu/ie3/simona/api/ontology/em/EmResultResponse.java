/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.EmData;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Message that provides em results to an external simulation. */
public record EmResultResponse(Map<UUID, List<EmData>> emResults)
    implements EmDataResponseMessageToExt {}
