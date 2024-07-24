/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.ontology;

/** Message returned to SIMONA indicating that the external simulation terminated */
public record TerminationCompleted(int phase) implements ControlResponseMessageFromExt {
  @Override
  public int getPhase() {
    return phase;
  }
}
