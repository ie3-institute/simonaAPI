/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.ev;

import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public interface EvcsModel {

  /**
   * @return the uuid of this evcs
   */
  UUID getUuid();

  /**
   * @return the id of this evcs
   */
  String getId();

  /**
   * @return the maximum AC apparent power rating of this evcs
   */
  ComparableQuantity<Power> getSRatedAC();

  /**
   * @return the maximum DC apparent or active power rating of this evcs
   */
  ComparableQuantity<Power> getPRatedDC();

  /**
   * @return the rated power factor (cos φ) of this evcs
   */
  double getCosPhiRated();
}
