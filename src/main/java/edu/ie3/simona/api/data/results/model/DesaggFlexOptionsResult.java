/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results.model;

import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public class DesaggFlexOptionsResult extends FlexOptionsResult {

  private final Map<String, FlexOptionsResult> connectedFlexOptionResults;

  public DesaggFlexOptionsResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax,
      Map<String, FlexOptionsResult> connectedFlexOptionResults) {
    super(time, inputModel, pRef, pMin, pMax);
    this.connectedFlexOptionResults = connectedFlexOptionResults;
  }

  public DesaggFlexOptionsResult(
      FlexOptionsResult flexOptionsResult,
      Map<String, FlexOptionsResult> connectedFlexOptionResults) {
    super(
        flexOptionsResult.getTime(),
        flexOptionsResult.getInputModel(),
        flexOptionsResult.getpRef(),
        flexOptionsResult.getpMin(),
        flexOptionsResult.getpMax());
    this.connectedFlexOptionResults = connectedFlexOptionResults;
  }

  public DesaggFlexOptionsResult(FlexOptionsResult flexOptionsResult) {
    super(
        flexOptionsResult.getTime(),
        flexOptionsResult.getInputModel(),
        flexOptionsResult.getpRef(),
        flexOptionsResult.getpMin(),
        flexOptionsResult.getpMax());
    this.connectedFlexOptionResults = Collections.emptyMap();
  }

  public Map<String, FlexOptionsResult> getConnectedFlexOptionResults() {
    return connectedFlexOptionResults;
  }
}
