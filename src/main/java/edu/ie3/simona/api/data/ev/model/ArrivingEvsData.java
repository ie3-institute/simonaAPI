/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.model;

import java.util.List;
import java.util.Optional;

public record ArrivingEvsData(List<EvModel> arrivals, Optional<Long> nextTick) {}
