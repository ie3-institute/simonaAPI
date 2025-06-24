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

public class FlexRequestResult extends ResultEntity {

  private final List<UUID> receivers;

  public FlexRequestResult(ZonedDateTime time, UUID inputModel, Collection<UUID> receivers) {
    super(time, inputModel);
    this.receivers = new ArrayList<>(receivers);
  }

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
