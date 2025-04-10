/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.Optional;
import java.util.UUID;

/**
 * Flex option request that will be sent to SIMONA.
 *
 * @param receiver uuid of the agent, that will receive the request
 * @param sender option for the uuid of the agent, that sent the request
 */
public record FlexOptionRequest(UUID receiver, Optional<UUID> sender) {}
