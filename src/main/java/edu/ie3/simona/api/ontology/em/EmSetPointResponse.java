/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.EmSetPoint;
import java.util.Map;
import java.util.UUID;

/**
 * Message that provides em data (set points) to an external simulation.
 *
 * @param emData map:receiver to em set point
 */
public record EmSetPointResponse(Map<UUID, EmSetPoint> emData)
    implements EmDataResponseMessageToExt {}
