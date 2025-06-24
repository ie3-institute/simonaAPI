/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.simulation;

/**
 * Message that the external simulation is activated with by SIMONA
 *
 * @param tick The current tick
 */
public record ActivationMessage(long tick) implements ControlMessageToExt {}
