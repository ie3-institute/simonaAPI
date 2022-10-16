/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology.builder;

import edu.ie3.simona.api.data.ev.model.EvModel;
import edu.ie3.simona.api.data.ev.ontology.EvMovementsMessage;
import java.util.*;

/** Builder for {@link EvMovementsMessage} */
public class EvcsMovementsMessageBuilder {
  private final Map<UUID, List<UUID>> departures;
  private final Map<UUID, List<EvModel>> arrivals;

  public EvcsMovementsMessageBuilder() {
    this.departures = new HashMap<>();
    this.arrivals = new HashMap<>();
  }

  public EvcsMovementsMessageBuilder addDeparture(UUID evcs, UUID ev) {
    departures.computeIfAbsent(evcs, k -> new LinkedList<>()).add(ev);
    return this;
  }

  public EvcsMovementsMessageBuilder addArrival(UUID evcs, EvModel ev) {
    arrivals.computeIfAbsent(evcs, k -> new LinkedList<>()).add(ev);
    return this;
  }

  /**
   * Create an {@link EvMovementsMessage} from all departures and arrivals that have been supplied
   * before.
   *
   * @return an {@link EvMovementsMessage}
   */
  public EvMovementsMessage build() {
    final Map<UUID, EvMovementsMessage.EvcsMovements> map = new HashMap<>();

    for (Map.Entry<UUID, List<UUID>> entry : departures.entrySet()) {
      List<UUID> dep = entry.getValue();
      List<EvModel> arr = arrivals.get(entry.getKey());
      if (arr == null) arr = new LinkedList<>();

      map.put(entry.getKey(), new EvMovementsMessage.EvcsMovements(dep, arr));
    }

    // all arrivals of evcs that have no departures
    for (Map.Entry<UUID, List<EvModel>> entry : arrivals.entrySet()) {
      if (!map.containsKey(entry.getKey()))
        map.put(
            entry.getKey(),
            new EvMovementsMessage.EvcsMovements(new LinkedList<>(), entry.getValue()));
    }

    return new EvMovementsMessage(map);
  }
}
