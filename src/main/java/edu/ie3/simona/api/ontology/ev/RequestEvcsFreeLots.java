/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.ev;

/** Request the number of free lots per charging station from SIMONA */
public record RequestEvcsFreeLots() implements EvDataMessageFromExt {}
