/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.simona.api.data.ontology.DataMessageFromExt;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.simulation.ontology.ControlResponseMessageFromExt;
import org.apache.pekko.actor.typed.ActorRef;

/**
 * Abstract base class for a connection between SIMONA and an external simulation with data flow
 * from external to SIMONA.
 */
public abstract class ExtInputDataConnection<M extends DataMessageFromExt>
    implements ExtDataConnection {

  /** Actor reference to service that handles data within SIMONA */
  private ActorRef<DataMessageFromExt> dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef<ControlResponseMessageFromExt> extSimAdapter;

  /**
   * Sets the actor refs for data and control flow
   *
   * @param extResultDataService actor ref to the adapter of the data service for data messages
   * @param extSimAdapter actor ref to the extSimAdapter
   */
  public final void setActorRefs(
      ActorRef<DataMessageFromExt> extResultDataService,
      ActorRef<ControlResponseMessageFromExt> extSimAdapter) {
    this.dataService = extResultDataService;
    this.extSimAdapter = extSimAdapter;
  }

  /**
   * Send information from the external simulation to SIMONA's external data service. Furthermore,
   * ExtSimAdapter within SIMONA is instructed to activate the external data service with the
   * current tick.
   *
   * @param msg the data/information that is sent to SIMONA's result data service
   */
  public final void sendExtMsg(M msg) {
    dataService.tell(msg);
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService));
  }
}
