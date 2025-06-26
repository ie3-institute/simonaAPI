/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Em set point result. */
public final class EmSetPointResult extends ResultEntity {

  private final Map<UUID, PValue> receiverToSetPoints;

  /**
   * Basic constructor of an em set point result.
   *
   * @param time date and time when the result is produced
   * @param sender uuid of the sending model
   * @param receiverToSetPoints map: uuid to set point
   */
  public EmSetPointResult(ZonedDateTime time, UUID sender, Map<UUID, PValue> receiverToSetPoints) {
    super(time, sender);
    this.receiverToSetPoints = receiverToSetPoints;
  }

  /** Returns the sender of the results. */
  public UUID getSender() {
    return getInputModel();
  }

  /** Returns the mapped (receiver to set point) set point. */
  public Map<UUID, PValue> getReceiverToSetPoint() {
    return receiverToSetPoints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EmSetPointResult that = (EmSetPointResult) o;
    return Objects.equals(receiverToSetPoints, that.receiverToSetPoints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), receiverToSetPoints);
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
