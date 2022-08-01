/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import java.util.*;

public class ProvideEvcsFreeLots implements ToExtEvSimDataResponseMessage {
  private final Map<UUID, Integer> evcs;

  /** No evcs available */
  public ProvideEvcsFreeLots() {
    this.evcs = new HashMap<>(0);
  }

  public ProvideEvcsFreeLots(Map<UUID, Integer> evcs) {
    this.evcs = evcs;
  }

  public Map<UUID, Integer> getEvcs() {
    return evcs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProvideEvcsFreeLots that = (ProvideEvcsFreeLots) o;
    return evcs.equals(that.evcs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(evcs);
  }
}
