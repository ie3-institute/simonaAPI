/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.value.PValue;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public sealed interface SetPoint extends EmData, EmMessageContent
    permits SetPoint.AggregatedSetPoint, SetPoint.DisaggregatedSetPoints {

  /**
   * Energy management set point that will be sent to SIMONA.
   *
   * @param receiver The receiver of the set point.
   * @param power An option for the em set point.
   */
  record AggregatedSetPoint(UUID receiver, Optional<PValue> power) implements SetPoint {
    /**
     * Constructor for {@link AggregatedSetPoint}.
     *
     * <p>Note: Using this constructor will signal SIMONA, that the current set point should be
     * kept.
     *
     * @param receiver The receiver of the set point.
     */
    public AggregatedSetPoint(UUID receiver) {
      this(receiver, Optional.empty());
    }

    /**
     * Constructor for {@link AggregatedSetPoint}.
     *
     * @param receiver The receiver of the set point.
     * @param p Power value of the set point.
     */
    public AggregatedSetPoint(UUID receiver, ComparableQuantity<Power> p) {
      this(receiver, Optional.of(new PValue(p)));
    }

    /**
     * Constructor for {@link AggregatedSetPoint}.
     *
     * @param receiver The receiver of the set point.
     * @param power Value of the set point.
     */
    public AggregatedSetPoint(UUID receiver, PValue power) {
      this(receiver, Optional.ofNullable(power));
    }
  }

  /**
   * Disaggregated energy management set points that will be sent to SIMONA.
   *
   * @param receiver The receiver of the set point.
   * @param disaggregated The disaggregated set point information.
   */
  record DisaggregatedSetPoints(UUID receiver, Map<UUID, PValue> disaggregated)
      implements SetPoint {}
}
