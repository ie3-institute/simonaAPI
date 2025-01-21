/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import static edu.ie3.util.quantities.PowerSystemUnits.PU;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.system.SystemParticipantResult;
import edu.ie3.simona.api.data.ExtDataContainer;
import java.util.Map;
import java.util.Optional;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/** Contains all results from SIMONA for a certain tick */
public class ExtResultContainer implements ExtDataContainer {

  /** Tick the results are meant for */
  private final long tick;

  /** Tick the external simulation can expect the next results */
  private final Optional<Long> maybeNextTick;

  /**
   * Map external id to result from SIMONA ATTENTION: The time stamp of the result entities is not
   * necessarily corresponding to the tick
   */
  private final Map<String, ModelResultEntity> simonaResultsMap;

  /**
   * Container class for result data from SIMONA
   *
   * @param tick current tick
   * @param simonaResultsMap results from SIMONA with external id as key
   * @param nextTick tick the external simulation can expect the next results
   */
  public ExtResultContainer(
      long tick, Map<String, ModelResultEntity> simonaResultsMap, Optional<Long> nextTick) {
    this.tick = tick;
    this.simonaResultsMap = simonaResultsMap;
    this.maybeNextTick = nextTick;
  }

  public ExtResultContainer(long tick, Map<String, ModelResultEntity> simonaResultsMap) {
    this(tick, simonaResultsMap, Optional.empty());
  }

  public Map<String, ModelResultEntity> getResults() {
    return simonaResultsMap;
  }

  public String getResultsAsString() { return resultMapToString(simonaResultsMap); }

  public Long getTick() {
    return tick;
  }

  public Optional<Long> getNextTick() {
    return maybeNextTick;
  }

  /**
   * Returns the result for a certain asset.
   */
  public ResultEntity getResult(String assetId) {
    return simonaResultsMap.get(assetId);
  }

  /**
   * Returns the voltage deviation in pu for certain asset, if this asset provided a {@link NodeResult}
   */
  public double getVoltageDeviation(String assetId) {
    if (simonaResultsMap.get(assetId) instanceof NodeResult nodeResult) {
      ComparableQuantity<Dimensionless> vMagDev =
          Quantities.getQuantity(-1.0, PU).add(nodeResult.getvMag());
      return vMagDev.getValue().doubleValue();
    } else {
      throw new IllegalArgumentException("VOLTAGE DEVIATION is only available for NodeResult's!");
    }
  }

  /**
   * Returns the voltage deviation for certain asset, if this asset provided a {@link NodeResult}
   */
  public double getVoltage(String assetId) {
    if (simonaResultsMap.get(assetId) instanceof NodeResult nodeResult) {
      return nodeResult.getvMag().getValue().doubleValue();
    } else {
      throw new IllegalArgumentException("VOLTAGE is only available for NodeResult's!");
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


  private String resultMapToString(
          Map<String, ModelResultEntity> results
  ) {
    StringBuilder resultString = new StringBuilder();
    for (String key : results.keySet()) {
      resultString.append("id = ").append(key).append(", time = ").append(results.get(key).getTime()).append(", result = ").append(results.get(key).getClass().getSimpleName()).append("\n");
    }
    return resultString.toString();
  }
}
