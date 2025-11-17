/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

/**
 * Tells the em service in SIMONA to handle all messages internally until the given tick.
 *
 * @param tick The tick for which an external handling will be provided.
 */
public record EmSimulationUntil(long tick) implements EmDataMessageFromExt {}
