/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import org.apache.pekko.actor.ActorRef;

/**
 * Interface for a connection between SIMONA and an external simulation with data flow from SIMONA
 * to external
 */
public interface ExtOutputDataConnection extends ExtDataConnection {

  /**
   * Sets the actor refs for data and control flow
   *
   * @param extResultDataService actor ref to the adapter of the data service for data messages
   * @param dataServiceActivation actor ref to the adapter of the data service for schedule
   *     activation messages
   * @param extSimAdapter actor ref to the extSimAdapter
   */
  void setActorRefs(
      ActorRef extResultDataService, ActorRef dataServiceActivation, ActorRef extSimAdapter);
}
