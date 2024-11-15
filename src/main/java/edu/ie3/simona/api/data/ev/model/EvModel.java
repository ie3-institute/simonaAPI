/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.model;

import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public interface EvModel {
  /**
   * @return the uuid of this ev
   */
  UUID getUuid();

  /**
   * @return the id of this ev
   */
  String getId();

  /**
   * @return the maximum AC charging power of this ev (as active power)
   */
  ComparableQuantity<Power> getPRatedAC();

  /**
   * @return the maximum DC charging power of this ev (as active power)
   */
  ComparableQuantity<Power> getPRatedDC();

  /**
   * @return the storage capacity of this ev's battery
   */
  ComparableQuantity<Energy> getEStorage();

  /**
   * @return the current energy charge of this ev's battery
   */
  ComparableQuantity<Energy> getStoredEnergy();

  /**
   * @return the departure tick of this ev
   */
  Long getDepartureTick();

  /**
   * @param newStoredEnergy the new stored energy
   * @return a copy of this ev model with given new stored energy
   */
  EvModel copyWith(ComparableQuantity<Energy> newStoredEnergy);
}
