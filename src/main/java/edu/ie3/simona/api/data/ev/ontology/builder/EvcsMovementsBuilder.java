/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology.builder;

import edu.ie3.simona.api.data.ev.model.EvModel;
import edu.ie3.simona.api.data.ev.ontology.EvcsMovementsMessage;
import java.util.*;

/** Builder for {@link EvcsMovementsMessage.EvcsMovements} */
public class EvcsMovementsBuilder {
  private final List<UUID> departures;
  private final List<EvModel> arrivals;

  public EvcsMovementsBuilder() {
    this.departures = new LinkedList<>();
    this.arrivals = new LinkedList<>();
  }

  public EvcsMovementsBuilder addDeparture(UUID ev) {
    departures.add(ev);
    return this;
  }

  public EvcsMovementsBuilder addArrival(EvModel ev) {
    arrivals.add(ev);
    return this;
  }

  /**
   * Create an EvcsMovements from all departures and arrivals that have been supplied before.
   *
   * @return an {@link EvcsMovementsMessage.EvcsMovements}
   */
  public EvcsMovementsMessage.EvcsMovements build() {
    return new EvcsMovementsMessage.EvcsMovements(departures, arrivals);
  }
}
