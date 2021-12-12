/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ontology;

import akka.actor.ActorRef;
import java.util.Objects;

public class ScheduleDataServiceMessage {
  private final ActorRef dataService;

  public ScheduleDataServiceMessage(ActorRef dataService) {
    this.dataService = dataService;
  }

  public ActorRef getDataService() {
    return dataService;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScheduleDataServiceMessage that = (ScheduleDataServiceMessage) o;
    return dataService.equals(that.dataService);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataService);
  }
}
