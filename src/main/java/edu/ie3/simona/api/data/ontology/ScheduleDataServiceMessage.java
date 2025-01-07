/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ontology;

import java.util.Objects;
import org.apache.pekko.actor.typed.ActorRef;

public class ScheduleDataServiceMessage<T extends DataMessageFromExt> {
  private final ActorRef<T> dataService;

  public ScheduleDataServiceMessage(ActorRef<T> dataService) {
    this.dataService = dataService;
  }

  public ActorRef<T> getDataService() {
    return dataService;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScheduleDataServiceMessage<?> that = (ScheduleDataServiceMessage<?>) o;
    return dataService.equals(that.dataService);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataService);
  }
}
