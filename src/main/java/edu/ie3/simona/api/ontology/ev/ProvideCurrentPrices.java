/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.ev;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Provides charging prices as a response to a {@link RequestCurrentPrices}.
 *
 * @param prices the charging prices per charging station
 */
public record ProvideCurrentPrices(Map<UUID, Double> prices) implements EvDataResponseMessageToExt {

  /** No prices available */
  public ProvideCurrentPrices() {
    this(new HashMap<>(0));
  }
}
