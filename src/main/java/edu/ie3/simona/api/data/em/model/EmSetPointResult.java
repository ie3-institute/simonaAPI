/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.UUID;

/** Em set point result. */
public class EmSetPointResult extends ResultEntity {

  private final PValue setPoint;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   */
  protected EmSetPointResult(ZonedDateTime time, UUID inputModel, PValue setPoint) {
    super(time, inputModel);
    this.setPoint = setPoint;
  }

  public PValue getSetPoint() {
    return setPoint;
  }
}
