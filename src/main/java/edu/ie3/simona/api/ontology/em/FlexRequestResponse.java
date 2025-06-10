/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.FlexRequestResult;
import java.util.Map;
import java.util.UUID;

/** Message that provides em data (flexibility requests) to an external simulation. */
public record FlexRequestResponse(Map<UUID, FlexRequestResult> flexRequests)
    implements EmDataResponseMessageToExt {}
