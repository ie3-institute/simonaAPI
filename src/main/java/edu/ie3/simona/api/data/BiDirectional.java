/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import edu.ie3.simona.api.data.ontology.DataMessageFromExt;
import edu.ie3.simona.api.data.ontology.DataResponseMessageToExt;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.simulation.ontology.ControlResponseMessageFromExt;
import org.apache.pekko.actor.typed.ActorRef;

/**
 * Enables bidirectional communication when extended by an external data connection.
 *
 * @param <M> type of message to SIMONA
 * @param <R> type of response messages to ext
 */
public abstract class BiDirectional<M extends DataMessageFromExt, R extends DataResponseMessageToExt>
    extends WithDataResponseToExt<R> implements ExtInputDataConnection<M> {

  protected BiDirectional() {
    super();
  }

  /** Actor reference to service that handles data within SIMONA */
  private ActorRef<DataMessageFromExt> dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef<ControlResponseMessageFromExt> extSimAdapter;

  @Override
  public void setActorRefs(
          ActorRef<DataMessageFromExt> dataService,
          ActorRef<ControlResponseMessageFromExt> extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

  @Override
  public void sendExtMsg(M msg) {
    dataService.tell(msg);
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService));
  }
}
