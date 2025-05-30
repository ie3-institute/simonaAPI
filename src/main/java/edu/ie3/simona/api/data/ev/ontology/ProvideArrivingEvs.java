/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import edu.ie3.simona.api.data.ev.model.EvModel;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Provide arriving EVs to SIMONA and its charging stations
 *
 * @param arrivals the arriving EVs per charging station UUID
 * @param maybeNextTick the next tick at which new arrivals are expected, or empty if simulation is
 *     about to end
 */
public record ProvideArrivingEvs(Map<UUID, List<EvModel>> arrivals, Optional<Long> maybeNextTick)
    implements EvDataMessageFromExt {}
