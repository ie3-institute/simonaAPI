/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev;

import akka.actor.ActorRef;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ev.model.EvModel;
import edu.ie3.simona.api.data.ev.ontology.*;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ExtEvData implements ExtData {
  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<EvDataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();
  /** Actor reference to service that handles ev data within SIMONA */
  private final ActorRef dataService;
  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private final ActorRef extSimAdapter;

  // important trigger queue must be the same as hold in actor
  // to make it safer one might consider asking the actor for ara reference on its trigger queue?!
  public ExtEvData(ActorRef dataService, ActorRef extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

  /**
   * Requests currently available evcs charging stations lots from SIMONA. This method blocks until
   * having received a response from SIMONA.
   *
   * @return a mapping from evcs uuid to the amount of available charging station lots
   */
  public Map<UUID, Integer> requestAvailablePublicEvcs() {
    sendExtMsg(new RequestEvcsFreeLots());

    try {
      // blocks until actor puts something here
      EvDataResponseMessageToExt evMessage = receiveTriggerQueue.take();

      if (evMessage.getClass().equals(ProvideEvcsFreeLots.class)) {
        final ProvideEvcsFreeLots provideEvcsFreeLots = (ProvideEvcsFreeLots) evMessage;
        return provideEvcsFreeLots.evcs();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    return new HashMap<>();
  }

  /**
   * Requests prices at all EVCS station at current tick. This method blocks until having received a
   * response from SIMONA.
   *
   * @return mapping from evcs uuid to current price
   */
  public Map<UUID, Double> requestCurrentPrices() {
    sendExtMsg(new RequestCurrentPrices());

    try {
      // blocks until actor puts something here
      EvDataResponseMessageToExt evMessage = receiveTriggerQueue.take();

      if (evMessage.getClass().equals(ProvideCurrentPrices.class)) {
        final ProvideCurrentPrices provideCurrentPrices = (ProvideCurrentPrices) evMessage;
        return provideCurrentPrices.prices();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    return new HashMap<>();
  }

  /**
   * Request the charged EVs that are departing from their charging stations at the current tick.
   * SIMONA returns the charged departing vehicles with updated battery SOC. This method blocks
   * until having received a response from SIMONA.
   *
   * @param departures the departing EV UUIDs per charging station UUID
   * @return all charged departing vehicles
   */
  public List<EvModel> requestDepartingEvs(Map<UUID, List<UUID>> departures) {
    sendExtMsg(new RequestDepartingEvs(departures));

    try {
      // blocks until actor puts something here
      EvDataResponseMessageToExt evMessage = receiveTriggerQueue.take();

      if (evMessage.getClass().equals(ProvideDepartingEvs.class)) {
        final ProvideDepartingEvs departedEvsResponse = (ProvideDepartingEvs) evMessage;
        return departedEvsResponse.departedEvs();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    return new ArrayList<>();
  }

  /**
   * Provide all EVs that are arriving at some charging station to SIMONA. Method returns right away
   * without expecting an answer from SIMONA.
   *
   * @param arrivals the arriving EV models per charging station UUID
   */
  public void provideArrivingEvs(Map<UUID, List<EvModel>> arrivals) {
    sendExtMsg(new ProvideArrivingEvs(arrivals));
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
   */
  public void queueExtResponseMsg(EvDataResponseMessageToExt extEvResponse) {
    try {
      receiveTriggerQueue.put(extEvResponse);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
