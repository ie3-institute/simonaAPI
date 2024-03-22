/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import edu.ie3.simona.api.data.ev.model.ArrivingEvsData;

import java.util.Map;
import java.util.UUID;

/**
 * Provide arriving EVs data to SIMONA and its charging stations
 *
 * @param arrivingEvsData the arriving EVs data per charging station UUID
 */
public record ProvideArrivingEvs(Map<UUID, ArrivingEvsData> arrivingEvsData)
    implements EvDataMessageFromExt {}
