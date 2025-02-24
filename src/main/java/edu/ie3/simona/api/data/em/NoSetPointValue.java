/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public class NoSetPointValue extends PValue {
  public NoSetPointValue(ComparableQuantity<Power> p) {
    super(p);
  }
}
