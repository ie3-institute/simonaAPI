/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.ontology;

import java.util.Objects;

public class ActivityStartTrigger implements ExtTrigger {

  private final long tick;

  public ActivityStartTrigger(long tick) {
    this.tick = tick;
  }

  public long getTick() {
    return tick;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ActivityStartTrigger that = (ActivityStartTrigger) o;
    return tick == that.tick;
  }

  @Override
  public int hashCode() {
    return Objects.hash(tick);
  }
}
