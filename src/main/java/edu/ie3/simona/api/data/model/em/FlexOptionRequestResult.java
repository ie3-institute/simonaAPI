/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/** Em flex request result. */
public final class FlexOptionRequestResult extends ResultEntity {

  /** The uuids of the receivers. */
  private final List<UUID> receivers;

  /**
   * Constructor of a {@link FlexOptionRequest}.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param receivers a collection of receivers
   */
  public FlexOptionRequestResult(ZonedDateTime time, UUID inputModel, Collection<UUID> receivers) {
    super(time, inputModel);
    this.receivers = new ArrayList<>(receivers);
  }

  /**
   * Returns the list of the uuids of all receivers, that should receive a request to provide flex
   * options.
   */
  public List<UUID> getReceivers() {
    return receivers;
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
        + '}';
  }
}
