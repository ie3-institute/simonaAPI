/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/** Em set point result. */
public class EmSetPointResult extends ResultEntity {

  private final Map<UUID, PValue> receiverToSetPoints;

  public EmSetPointResult(ZonedDateTime time, UUID sender, Map<UUID, PValue> receiverToSetPoints) {
    super(time, sender);
    this.receiverToSetPoints = receiverToSetPoints;
  }

  public UUID getSender() {
    return getInputModel();
  }

  public Map<UUID, PValue> getReceiverToSetPoint() {
    return receiverToSetPoints;
  }

  @Override
  public String toString() {
    return "EmSetPointResult{"
            + "time="
            + getTime()
            + ", sender="
            + getInputModel()
            + ", receiverToSetPoints="
            + receiverToSetPoints
            + '}';
  }
}
