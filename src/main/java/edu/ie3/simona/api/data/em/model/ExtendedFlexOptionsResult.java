/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public class ExtendedFlexOptionsResult extends FlexOptionsResult {

  private final UUID receiver;

  private final Map<UUID, FlexOptionsResult> disaggregated;

  /**
   * Standard constructor for {@link ExtendedFlexOptionsResult}.
   *
   * @param time date and time when the result is produced
   * @param sender uuid of the input model that produces the result
   * @param receiver uuid of the receiver that will receive this result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   */
  public ExtendedFlexOptionsResult(
      ZonedDateTime time,
      UUID sender,
      UUID receiver,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(time, sender, pRef, pMin, pMax);
    this.receiver = receiver;
    this.disaggregated = Collections.emptyMap();
  }

  /**
   * Constructor for {@link ExtendedFlexOptionsResult} with disaggregated flex options.
   *
   * @param time date and time when the result is produced
   * @param sender uuid of the input model that produces the result
   * @param receiver uuid of the receiver that will receive this result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   */
  public ExtendedFlexOptionsResult(
      ZonedDateTime time,
      UUID sender,
      UUID receiver,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax,
      Map<UUID, FlexOptionsResult> disaggregated) {
    super(time, sender, pRef, pMin, pMax);
    this.receiver = receiver;
    this.disaggregated = disaggregated;
  }

  public UUID getSender() {
    return getInputModel();
  }

  public UUID getReceiver() {
    return receiver;
  }

  public boolean hasDisaggregated() {
    return !disaggregated.isEmpty();
  }

  public Map<UUID, FlexOptionsResult> getDisaggregated() {
    return Collections.unmodifiableMap(disaggregated);
  }

  @Override
  public String toString() {
    return "ExtendedFlexOptionsResult{"
        + "time="
        + getTime()
        + ", sender="
        + getSender()
        + ", receiver="
        + receiver
        + ", pRef="
        + getpRef()
        + ", pMin="
        + getpMin()
        + ", pMax="
        + getpMax()
        + ", disaggregated="
        + disaggregated
        + '}';
  }
}
