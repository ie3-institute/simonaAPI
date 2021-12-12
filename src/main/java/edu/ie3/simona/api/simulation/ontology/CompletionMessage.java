/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.ontology;

import java.util.List;
import java.util.Objects;

public class CompletionMessage implements ExtTriggerResponse {
  private final List<Long> newTriggers;

  public CompletionMessage(List<Long> newTriggers) {
    this.newTriggers = newTriggers;
  }

  public List<Long> getNewTriggers() {
    return newTriggers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CompletionMessage that = (CompletionMessage) o;
    return newTriggers.equals(that.newTriggers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(newTriggers);
  }
}
