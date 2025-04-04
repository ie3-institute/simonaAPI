/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results.ontology;

/** Request calculated results from SIMONA in the current tick */
public record RequestResultEntities(Long tick) implements ResultDataMessageFromExt {}
