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
  public final LinkedBlockingQueue<ExtEvResponseMessage> receiveTriggerQueue =
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
   * Requests available evcs charging stations.
   *
   * <p>todo: What does available mean exactly? Currently vs. Generally available
   *
   * @return a mapping from evcs uuid to the amount of available charging stations
   */
  public Map<UUID, Integer> requestAvailablePublicEvCs() {
    sendExtMsg(new RequestEvcsFreeLots());

    try {
      // blocks until actor puts something here
      ExtEvResponseMessage evMessage = receiveTriggerQueue.take();

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
   * Exchange all ev movements with SIMONA which consists of departing and arriving evs at a certain
   * tick. SIMONA takes over arrived parking vehicles and returns charged departing vehicles.
   *
   * <p>todo: How is the information about the current tick conveyed? EvModels only carry departure
   * not arrival tick.
   *
   * @param evMovementsMessage the ev movements for ev exchange
   * @return all charged departed vehicles
   */
  public List<EvModel> exchangeEvArrivalsAndDepartures(EvMovementsMessage evMovementsMessage) {
    sendExtMsg(evMovementsMessage);

    try {
      // blocks until actor puts something here
      ExtEvResponseMessage evMessage = receiveTriggerQueue.take();

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
      ExtEvResponseMessage evMessage = receiveTriggerQueue.take();

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
   * Sends information from the external ev simulation to SIMONAs ev data service. Furthermore
   * instructs the ext sim adapter within SIMONA to activate the ev data service.
   *
   * @param msg the data/information that is sent to SIMONA's ev data service
   */
  public void sendExtMsg(ExtEvMessage msg) {
    dataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
  }

  public void queueExtResponseMsg(ExtEvResponseMessage extEvResponse) {
    try {
      receiveTriggerQueue.put(extEvResponse);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
