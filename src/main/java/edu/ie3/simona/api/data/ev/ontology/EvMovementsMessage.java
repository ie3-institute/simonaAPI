/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import edu.ie3.simona.api.data.ev.model.EvModel;
import java.util.*;

public class EvMovementsMessage implements ExtEvMessage {
  private final Map<UUID, EvcsMovements> movements;

  public EvMovementsMessage(Map<UUID, EvcsMovements> movements) {
    this.movements = movements;
  }

  public Map<UUID, EvcsMovements> getMovements() {
    return movements;
  }

  public static final class EvcsMovements {
    private final List<UUID> departures;
    private final List<EvModel> arrivals;

    public EvcsMovements(List<UUID> departures, List<EvModel> arrivals) {
      this.departures = departures;
      this.arrivals = arrivals;
    }

    public List<UUID> getDepartures() {
      return departures;
    }

    public List<EvModel> getArrivals() {
      return arrivals;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      EvcsMovements that = (EvcsMovements) o;
      return departures.equals(that.departures) && arrivals.equals(that.arrivals);
    }

    @Override
    public int hashCode() {
      return Objects.hash(departures, arrivals);
    }
  }
}
