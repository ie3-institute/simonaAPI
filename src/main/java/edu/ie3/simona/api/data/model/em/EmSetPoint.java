/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.value.PValue;
import java.util.Optional;
import java.util.UUID;

/**
 * Energy management set point that will be sent to SIMONA.
 *
 * @param receiver uuid of the agent, that will receive the set point
 * @param pValue option for the power value
 */
public record EmSetPoint(UUID receiver, Optional<PValue> pValue) {}
