/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.simona.api.data.ontology.DataMessageFromExt;
import edu.ie3.simona.api.simulation.ontology.ControlResponseMessageFromExt;
import org.apache.pekko.actor.typed.ActorRef;
import edu.ie3.simona.api.data.ontology.DataMessageFromExt;

/**
 * Interface for a connection between SIMONA and an external simulation with data flow from SIMONA
 * to external.
 */
public interface ExtOutputDataConnection<M extends DataMessageFromExt> extends ExtDataConnection {

  /**
   * Sets the actor refs for data and control flow
   *
   * @param extResultDataService actor ref to the adapter of the data service for data messages
   * @param dataServiceActivation actor ref to the adapter of the data service for schedule
   *     activation messages
   * @param extSimAdapter actor ref to the extSimAdapter
   */
  void setActorRefs(
      ActorRef<DataMessageFromExt> extResultDataService,
      ActorRef<DataMessageFromExt> dataServiceActivation,
      ActorRef<ControlResponseMessageFromExt> extSimAdapter);
  /**
   * Send information from the external simulation to SIMONA's external data service. Furthermore,
   * ExtSimAdapter within SIMONA is instructed to activate the external data service with the
   * current tick.
   *
   * @param msg the data/information that is sent to SIMONA's result data service
   */
  void sendExtMsg(M msg);
}
