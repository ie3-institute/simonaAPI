/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.value.PValue;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Energy management set point that will be sent to SIMONA.
 *
 * @param receiver The receiver of the set point.
 * @param power An option for the em set point.
 */
public record EmSetPoint(UUID receiver, Optional<PValue> power, Map<UUID, PValue> disaggregated)
    implements EmData {
  /**
   * Constructor for {@link EmSetPoint}.
   *
   * <p>Note: Using this constructor will signal SIMONA, that the current set point should be kept.
   *
   * @param receiver The receiver of the set point.
   */
  public EmSetPoint(UUID receiver) {
    this(receiver, Optional.empty(), Collections.emptyMap());
  }

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * @param receiver The receiver of the set point.
   * @param p Power value of the set point.
   */
  public EmSetPoint(UUID receiver, ComparableQuantity<Power> p) {
    this(receiver, Optional.of(new PValue(p)), Collections.emptyMap());
  }

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * @param receiver The receiver of the set point.
   * @param power Value of the set point.
   */
  public EmSetPoint(UUID receiver, PValue power) {
    this(receiver, Optional.ofNullable(power), Collections.emptyMap());
  }

  /**
   * Constructor for {@link EmSetPoint}.
   *
   * @param receiver The receiver of the set point.
   * @param power Value of the set point.
   * @param disaggregated Map: uuid to disaggregated set points.
   */
  public EmSetPoint(UUID receiver, PValue power, Map<UUID, PValue> disaggregated) {
    this(receiver, Optional.of(power), disaggregated);
  }
}
