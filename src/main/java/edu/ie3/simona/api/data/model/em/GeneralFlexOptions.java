/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.util.interval.ClosedInterval;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * General flex options that can represent various flex option types.
 *
 * @param model That is providing this flex options.
 * @param flexType The type of the flex options.
 * @param pMin The minimal power.
 * @param pMax The maximal power.
 * @param etaCharge The charging losses in percent.
 * @param etaDischarge The discharging losses in percent.
 * @param tickToEnergyLimits A map: tick to energy limits.
 * @param disaggregated A map: uuid to disaggregated flex options.
 */
public record GeneralFlexOptions(
    UUID receiver,
    UUID model,
    String flexType,
    ComparableQuantity<Power> pMin,
    ComparableQuantity<Power> pMax,
    ComparableQuantity<Dimensionless> etaCharge,
    ComparableQuantity<Dimensionless> etaDischarge,
    Map<Long, ClosedInterval<ComparableQuantity<Energy>>> tickToEnergyLimits,
    Map<UUID, FlexOptions> disaggregated)
    implements FlexOptions {

  public GeneralFlexOptions(
      UUID receiver,
      UUID model,
      String flexType,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax,
      ComparableQuantity<Dimensionless> etaCharge,
      ComparableQuantity<Dimensionless> etaDischarge,
      Map<Long, ClosedInterval<ComparableQuantity<Energy>>> tickToEnergyLimits) {
    this(
        receiver,
        model,
        flexType,
        pMin,
        pMax,
        etaCharge,
        etaDischarge,
        tickToEnergyLimits,
        new HashMap<>());
  }

  @Override
  public UUID receiver() {
    return receiver;
  }

  @Override
  public void addDisaggregated(UUID model, FlexOptions flexOptions) {
    disaggregated.put(model, flexOptions);
  }
}
