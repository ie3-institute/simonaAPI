/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import static edu.ie3.util.quantities.PowerSystemUnits.PU;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.system.SystemParticipantResult;
import edu.ie3.simona.api.data.ExtDataPackage;
import java.util.Map;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/** Contains all results from SIMONA for a certain tick */
public class ExtResultPackage implements ExtDataPackage {

  /** Tick the package is meant for */
  private final Long tick;

  /**
   * Map external id to result from SIMONA ATTENTION: The time stamp of the result entities is not
   * necessarily corresponding to the tick
   */
  private final Map<String, ModelResultEntity> simonaResultsMap;

  /**
   * Container class for output data from SIMONA
   *
   * @param tick current tick
   * @param simonaResultsMap results from SIMONA with external id as key
   */
  public ExtResultPackage(Long tick, Map<String, ModelResultEntity> simonaResultsMap) {
    this.tick = tick;
    this.simonaResultsMap = simonaResultsMap;
  }

  public Long getTick() {
    return tick;
  }

  /**
   * Returns the voltage deviation for certain asset, if this asset provided a {@link NodeResult}
   */
  public double getVoltageDeviation(String assetId) {
    if (simonaResultsMap.get(assetId) instanceof NodeResult nodeResult) {
      ComparableQuantity<Dimensionless> vMagDev =
          Quantities.getQuantity(0, PU)
              .add(nodeResult.getvMag().subtract(Quantities.getQuantity(1.0, PU)));
      return vMagDev.getValue().doubleValue();
    } else {
      throw new IllegalArgumentException("VOLTAGE DEVIATION is only available for NodeResult's!");
    }
  }

  /**
   * Returns the active power in kW for certain asset, if this asset provided a {@link
   * SystemParticipantResult}
   */
  public double getActivePower(String assetId) {
    if (simonaResultsMap.get(assetId) instanceof SystemParticipantResult systemParticipantResult) {
      return systemParticipantResult.getP().getValue().doubleValue();
    } else {
      throw new IllegalArgumentException(
          "ACTIVE POWER is only available for SystemParticipantResult's!");
    }
  }

  /**
   * Returns the reactive power in kVAr for certain asset, if this asset provided a {@link
   * SystemParticipantResult}
   */
  public double getReactivePower(String assetId) {
    if (simonaResultsMap.get(assetId) instanceof SystemParticipantResult systemParticipantResult) {
      return systemParticipantResult.getQ().getValue().doubleValue();
    } else {
      throw new IllegalArgumentException(
          "REACTIVE POWER is only available for SystemParticipantResult's!");
    }
  }

  /** Returns the line loading for certain asset, if this asset provided a {@link LineResult} */
  public double getLineLoading(String assetId) {
    throw new IllegalArgumentException("LINE LOADING is not implemented yet!");
  }
}
