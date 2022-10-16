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
  public final LinkedBlockingQueue<ToExtEvSimDataResponseMessage> receiveTriggerQueue =
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
   * Requests currently available evcs charging stations lots from SIMONA.
   *
   * @return a mapping from evcs uuid to the amount of available charging station lots
   */
  public Map<UUID, Integer> requestAvailablePublicEvcs() {
    sendExtMsg(new RequestEvcsFreeLots());

    try {
      // blocks until actor puts something here
      ToExtEvSimDataResponseMessage evMessage = receiveTriggerQueue.take();

      if (evMessage.getClass().equals(ProvideEvcsFreeLots.class)) {
        final ProvideEvcsFreeLots provideEvcsFreeLots = (ProvideEvcsFreeLots) evMessage;
        return provideEvcsFreeLots.getEvcs();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    return new HashMap<>();
  }

  /**
   * Exchange all ev movements with SIMONA, which consist of departing and arriving evs at the
   * current tick. SIMONA receives and handles arriving parking vehicles and returns charged
   * departing vehicles.
   *
   * @param evMovementsMessage the ev movements for ev exchange
   * @return all charged departed vehicles
   */
  public List<EvModel> exchangeEvcsArrivalsAndDepartures(EvMovementsMessage evMovementsMessage) {
    sendExtMsg(evMovementsMessage);

    try {
      // blocks until actor puts something here
      ToExtEvSimDataResponseMessage evMessage = receiveTriggerQueue.take();

      if (evMessage.getClass().equals(AllDepartedEvsResponse.class)) {
        final AllDepartedEvsResponse departedEvsResponse = (AllDepartedEvsResponse) evMessage;
        return departedEvsResponse.getDepartedEvs();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    return new ArrayList<>();
  }

  /**
   * Requests prices at all EVCS station at current tick.
   *
   * @return mapping from evcs uuid to current price
   */
  public Map<UUID, Double> requestCurrentPrices() {
    sendExtMsg(new RequestCurrentPrices());

    try {
      // blocks until actor puts something here
      ToExtEvSimDataResponseMessage evMessage = receiveTriggerQueue.take();

      if (evMessage.getClass().equals(ProvideCurrentPrices.class)) {
        final ProvideCurrentPrices provideCurrentPrices = (ProvideCurrentPrices) evMessage;
        return provideCurrentPrices.getPrices();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    return new HashMap<>();
  }

  /**
   * Send information from the external ev simulation to SIMONA's ev data service. Furthermore,
   * ExtSimAdapter within SIMONA is instructed to activate the ev data service with the current
   * tick.
   *
   * @param msg the data/information that is sent to SIMONA's ev data service
   */
  public void sendExtMsg(FromExtEvSimDataMessage msg) {
    dataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
  }

  /**
   * Queues message from SIMONA that should be handled by the external ev simulation.
   *
   * @param extEvResponse the message to be handled
   */
  public void queueExtResponseMsg(ToExtEvSimDataResponseMessage extEvResponse) {
    try {
      receiveTriggerQueue.put(extEvResponse);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
