/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.datamodel.models.input.AssetInput;
import java.util.List;
import org.apache.pekko.actor.ActorRef;

public interface ExtInputDataConnection extends ExtDataConnection {

  /**
   * Sets the actor refs for data and control flow.
   *
   * @param dataService actor ref to the adapter of the data service for schedule activation
   *     messages
   * @param extSimAdapter actor ref to the extSimAdapter
   */
  void setActorRefs(ActorRef dataService, ActorRef extSimAdapter);

  /** Returns a list of all classes this connection provides data for. */
  List<Class<? extends AssetInput>> getTargetClasses();
}
