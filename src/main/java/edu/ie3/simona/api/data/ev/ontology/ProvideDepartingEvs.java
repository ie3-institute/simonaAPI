/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import edu.ie3.simona.api.data.ev.model.EvModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides EV models of EVs that are leaving their parking spaces at the current tick as a response
 * to a {@link RequestDepartingEvs}.
 *
 * @param departedEvs the EVs that have left the charging stations at the current tick
 */
public record ProvideDepartingEvs(List<EvModel> departedEvs) implements EvDataResponseMessageToExt {

  /** No departed evs during current tick */
  public ProvideDepartingEvs() {
    this(new ArrayList<>(0));
  }
}
