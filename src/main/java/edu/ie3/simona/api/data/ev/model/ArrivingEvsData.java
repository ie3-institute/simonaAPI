/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.model;

import java.util.List;
import java.util.Optional;

/**
 * Data including arriving EVs and next arrival tick for a single charging station. Arriving EVs
 * cannot be provided without being scheduled first.
 *
 * @param arrivals The arriving EVs for the current station
 * @param maybeNextTick The optional next tick at which some interaction between EV simulation and
 *     charging station is expected. Empty if no next event is known at this moment.
 */
public record ArrivingEvsData(List<EvModel> arrivals, Optional<Long> maybeNextTick) {}
