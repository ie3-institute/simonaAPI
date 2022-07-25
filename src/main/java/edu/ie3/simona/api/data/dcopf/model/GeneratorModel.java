/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.dcopf.model;

import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public interface GeneratorModel {
  /** @return the uuid of this generator (SIMONA) */
  UUID getUuid();

  /** @return the id of this generator (MATPOWER) */
  String getId();

  /** @return the current active power setpoint */
  ComparableQuantity<Power> getSetpoint();

  /**
   * @param newSetpoint the new stored energy
   * @return a copy of this ev model with given new stored energy
   */
  GeneratorModel copyWith(ComparableQuantity<Power> newSetpoint);
}
