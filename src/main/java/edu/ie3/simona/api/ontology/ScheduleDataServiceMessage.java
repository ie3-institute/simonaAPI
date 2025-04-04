/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology;

import edu.ie3.simona.api.ontology.simulation.ControlResponseMessageFromExt;
import org.apache.pekko.actor.typed.ActorRef;

public record ScheduleDataServiceMessage(ActorRef<DataMessageFromExt> dataService)
    implements ControlResponseMessageFromExt {}
