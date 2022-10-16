/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import java.util.Map;
import java.util.UUID;

/**
 * Request EVs that are departing from charging stations at the current tick in SIMONA.
 *
 * @param departures the departing EVs' UUIDs per charging station UUID
 */
public record RequestDepartingEvs(Map<UUID, UUID> departures) implements EvDataMessageFromExt {}
