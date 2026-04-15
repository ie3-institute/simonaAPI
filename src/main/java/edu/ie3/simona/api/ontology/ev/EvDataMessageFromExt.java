/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.ev;

import edu.ie3.simona.api.ontology.DataMessageFromExt;

public sealed interface EvDataMessageFromExt extends DataMessageFromExt
    permits ProvideArrivingEvs, RequestCurrentPrices, RequestDepartingEvs, RequestEvcsFreeLots {}
