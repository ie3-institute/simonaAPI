/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev;

import edu.ie3.simona.api.data.ExtInputDataConnection;
import edu.ie3.simona.api.data.ev.model.EvModel;
import edu.ie3.simona.api.data.ev.ontology.*;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.pekko.actor.ActorRef;

public class ExtEvDataConnection implements ExtInputDataConnection {
  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<EvDataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  /** Actor reference to service that handles ev data within SIMONA */
  private ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef extSimAdapter;

  // important trigger queue must be the same as hold in actor
  // to make it safer one might consider asking the actor for ara reference on its trigger queue?!
  public ExtEvDataConnection() {}

  @Override
  public void setActorRefs(ActorRef dataService, ActorRef extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

  /**
   * Requests currently available evcs charging stations lots from SIMONA. This method blocks until
   * having received a response from SIMONA.
   *
   * @return a mapping from evcs uuid to the amount of available charging station lots
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public Map<UUID, Integer> requestAvailablePublicEvcs() throws InterruptedException {
    sendExtMsg(new RequestEvcsFreeLots());

    return receiveWithType(ProvideEvcsFreeLots.class).evcs();
  }

  /**
   * Requests prices at all EVCS station at current tick. This method blocks until having received a
   * response from SIMONA.
   *
   * @return mapping from evcs uuid to current price
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public Map<UUID, Double> requestCurrentPrices() throws InterruptedException {
    sendExtMsg(new RequestCurrentPrices());

    return receiveWithType(ProvideCurrentPrices.class).prices();
  }

  /**
   * Request the charged EVs that are departing from their charging stations at the current tick.
   * SIMONA returns the charged departing vehicles with updated battery SOC. This method blocks
   * until having received a response from SIMONA.
   *
   * @param departures the departing EV UUIDs per charging station UUID
   * @return all charged departing vehicles
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public List<EvModel> requestDepartingEvs(Map<UUID, List<UUID>> departures)
      throws InterruptedException {
    sendExtMsg(new RequestDepartingEvs(departures));

    return receiveWithType(ProvideDepartingEvs.class).departedEvs();
  }

  /**
   * Provide all EVs that are arriving at some charging station to SIMONA. Method returns right away
   * without expecting an answer from SIMONA.
   *
   * @param arrivals the arriving EV models per charging station UUID
   * @param maybeNextTick the next tick at which new arrivals are expected, or empty if simulation
   *     is about to end
   */
  public void provideArrivingEvs(Map<UUID, List<EvModel>> arrivals, Optional<Long> maybeNextTick) {
    sendExtMsg(new ProvideArrivingEvs(arrivals, maybeNextTick));
  }

  /**
   * Send information from the external ev simulation to SIMONA's ev data service. Furthermore,
   * ExtSimAdapter within SIMONA is instructed to activate the ev data service with the current
   * tick.
   *
   * @param msg the data/information that is sent to SIMONA's ev data service
   */
  public void sendExtMsg(EvDataMessageFromExt msg) {
    dataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
  }

  /**
   * Queues message from SIMONA that should be handled by the external ev simulation.
   *
   * @param extEvResponse the message to be handled
   * @throws InterruptedException if the thread running this has been interrupted during waiting for
   *     the message to be queued
   */
  public void queueExtResponseMsg(EvDataResponseMessageToExt extEvResponse)
      throws InterruptedException {
    receiveTriggerQueue.put(extEvResponse);
  }

  /**
   * Waits until a message of given type is added to the queue. If the message has a different type,
   * a RuntimeException is thrown. This method blocks until having received a response from SIMONA.
   *
   * @param expectedMessageClass the expected class of the message to be received
   * @return a message of the expected type once it has been received
   * @param <T> the type of the expected message
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  @SuppressWarnings("unchecked")
  private <T extends EvDataResponseMessageToExt> T receiveWithType(Class<T> expectedMessageClass)
      throws InterruptedException {

    // blocks until actor puts something here
    EvDataResponseMessageToExt evMessage = receiveTriggerQueue.take();

    if (evMessage.getClass().equals(expectedMessageClass)) {
      return (T) evMessage;
    } else
      throw new RuntimeException(
          "Received unexpected message '"
              + evMessage
              + "', expected type '"
              + expectedMessageClass
              + "'");
  }
}
