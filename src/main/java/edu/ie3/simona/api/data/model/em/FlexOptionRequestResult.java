/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.*;

/** Em flex request result. */
public final class FlexOptionRequestResult extends ResultEntity {

  /** The uuids of the receivers. */
  private final List<UUID> receivers;

    /**
     * True, if disaggregated flex option should be returned.
     */
  private final boolean disaggregated;

  /**
   * Constructor of a {@link FlexOptionRequest}.
   *
   * @param time date and time when the result is produced
   * @param sender uuid of the input model that produces the result
   * @param receivers a collection of receivers
   * @param disaggregated if true, disaggregated flex option are requested
   */
  public FlexOptionRequestResult(ZonedDateTime time, UUID sender, Collection<UUID> receivers, boolean disaggregated) {
    super(time, sender);
    this.receivers = new ArrayList<>(receivers);
    this.disaggregated = disaggregated;
  }

  /** Returns the uuid of the sender. */
  public UUID getSender() {
    return getInputModel();
  }

  /**
   * Returns the list of the uuids of all receivers, that should receive a request to provide flex
   * options.
   */
  public List<UUID> getReceivers() {
    return receivers;
  }

    /**
     *
     * Returns true, if disaggregated flex options should be returned.
     */
  public boolean isDisaggregated() {
      return disaggregated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FlexOptionRequestResult that = (FlexOptionRequestResult) o;
    return Objects.equals(receivers, that.receivers) && Objects.equals(disaggregated, that.disaggregated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), receivers);
  }

  @Override
  public String toString() {
    return "FlexRequestResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", receivers="
        + receivers
            + ", disaggregated="
            + disaggregated
        + '}';
  }
}
