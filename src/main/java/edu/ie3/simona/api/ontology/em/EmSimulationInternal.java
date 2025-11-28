/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

/**
 * Request send to SIMONA to simulate ems internally for the given tick.
 *
 * @param tick for which the em service should simulate internally
 */
public record EmSimulationInternal(long tick) implements EmDataMessageFromExt {}
