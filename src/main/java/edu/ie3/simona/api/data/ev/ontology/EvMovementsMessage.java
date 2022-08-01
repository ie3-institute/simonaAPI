/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import edu.ie3.simona.api.data.ev.model.EvModel;
import java.util.*;

/**
 * Message that contains all ev movements for a certain tick. These consist of arrivals and
 * departures. Departures are cars (represented by their uuids) that leave their parking space so
 * have to be handed back from SIMONA to the EvSimulation. Arrivals are cars which are parking at
 * this certain and are handed over to SIMONA.
 *
 * @param movements the movements which are communicated
 */
public record EvMovementsMessage(Map<UUID, EvcsMovements> movements) implements ExtEvMessage {

  public record EvcsMovements(List<UUID> departures, List<EvModel> arrivals) {

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof EvcsMovements that)) return false;
      return departures.equals(that.departures) && arrivals.equals(that.arrivals);
    }
  }
}
