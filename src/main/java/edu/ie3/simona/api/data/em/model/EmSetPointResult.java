/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/** Em set point result. */
public class EmSetPointResult extends ResultEntity {

  private final PValue setPoint;

  public EmSetPointResult(ZonedDateTime time, UUID inputModel, Optional<PValue> setPoint) {
    super(time, inputModel);
    this.setPoint = setPoint.orElse(null);
  }

  public Optional<PValue> getSetPoint() {
    return Optional.ofNullable(setPoint);
  }
}
