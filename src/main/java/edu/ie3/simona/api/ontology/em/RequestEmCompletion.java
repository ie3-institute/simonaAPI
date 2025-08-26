/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

/**
 * Request send to SIMONA to finish the em service.
 *
 * @param tick for which the em service should be finished
 */
public record RequestEmCompletion(long tick) implements EmDataMessageFromExt {}
