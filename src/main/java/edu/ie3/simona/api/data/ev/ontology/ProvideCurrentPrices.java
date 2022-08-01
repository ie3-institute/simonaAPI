/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import java.util.*;

public class ProvideCurrentPrices implements ToExtEvSimDataResponseMessage {
  private final Map<UUID, Double> prices;

  /** No prices available */
  public ProvideCurrentPrices() {
    this.prices = new HashMap<>(0);
  }

  public ProvideCurrentPrices(Map<UUID, Double> prices) {
    this.prices = prices;
  }

  public Map<UUID, Double> getPrices() {
    return prices;
  }
}
