/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.util.interval.ClosedInterval;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Energy boundaries flex options that can represent various flex option types.
 *
 * @param receiver Of the flex options.
 * @param model That is providing this flex options.
 * @param energyBoundaries The energy boundaries.
 */
public record EnergyBoundariesFlexOptions(
    UUID receiver, UUID model, List<AssetEnergyBoundaries> energyBoundaries)
    implements FlexOptions {

  /**
   * Energy boundaries for an asset. The energy limits (valid for the interval from tick to the
   * next) constitute the boundaries between which flexibility can be used.
   *
   * @param energyLimits Energy limits that signify the potential upwards and downwards flexibility
   *     potential for the respective tick. The energy limits for all ticks relate to the energy
   *     potential at the current tick (which is defined to be zero).
   * @param powerLimits The power limits, which limit the power of the complete asset for all time
   *     steps. If energy limits (upper and lower) are the same at some time step, power limits are
   *     ignored.
   * @param etaCharge The charging efficiency.
   * @param etaDischarge The discharging efficiency.
   * @param tickDisconnect Optionally, the tick at which the storage will be disconnected, thus the
   *     upward or downward energy potential can not be used beyond this tick.
   */
  public record AssetEnergyBoundaries(
      Map<Long, ClosedInterval<ComparableQuantity<Energy>>> energyLimits,
      ClosedInterval<ComparableQuantity<Power>> powerLimits,
      ComparableQuantity<Dimensionless> etaCharge,
      ComparableQuantity<Dimensionless> etaDischarge,
      OptionalLong tickDisconnect) {

    public AssetEnergyBoundaries(
        Map<Long, ClosedInterval<ComparableQuantity<Energy>>> energyLimits,
        ClosedInterval<ComparableQuantity<Power>> powerLimits) {
      this(
          energyLimits,
          powerLimits,
          Quantities.getQuantity(1d, PowerSystemUnits.PU),
          Quantities.getQuantity(1d, PowerSystemUnits.PU),
          OptionalLong.empty());
    }
  }
}
