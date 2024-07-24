/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.ontology;

/**
 * Message that the external simulation is activated with by SIMONA
 *
 * @param tick The current tick
 * @param phase Phase of the simulation
 */
public record ActivationMessage(long tick, int phase) implements ControlMessageToExt {
  @Override
  public int getPhase() {
    return phase;
  }
}
