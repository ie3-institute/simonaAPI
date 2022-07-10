/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev;

import akka.actor.ActorRef;
import edu.ie3.simona.api.data.ExtDataInterface;
import edu.ie3.simona.api.data.ev.model.EvModel;
import edu.ie3.simona.api.data.ev.ontology.*;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ExtEvDataInterface implements ExtDataInterface {
  public final LinkedBlockingQueue<ToExtEvSimDataMessage> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  /** Actor reference to service that handles ev data within SIMONA */
  private final ActorRef evDataService;
  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private final ActorRef extSimAdapter;

  // important trigger queue must be the same as hold in actor
  // to make it safer one might consider asking the actor for ara reference on its trigger queue?!
  public ExtEvDataInterface(ActorRef evDataService, ActorRef extSimAdapter) {
    this.evDataService = evDataService;
    this.extSimAdapter = extSimAdapter;
  }

  /**
   * Requests available evcs charging stations.
   *
   * <p>todo: What does available mean exactly? Currently vs. Generally available
   *
   * @return a mapping from evcs uuid to the amount of available chraging stations
   */
  public Map<UUID, Integer> requestAvailablePublicEvCs() {
    sendExtMsg(new RequestEvcsFreeLots());

    try {
      // blocks until actor puts something here
      ToExtEvSimDataMessage evMessage = receiveTriggerQueue.take();

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
      ToExtEvSimDataMessage evMessage = receiveTriggerQueue.take();

      if (evMessage.getClass().equals(AllDepartedEvsResponse.class)) {
        final AllDepartedEvsResponse departedEvsResponse = (AllDepartedEvsResponse) evMessage;
        // todo sanity check that the expected evs got returned?
        return departedEvsResponse.getDepartedEvs();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    return new ArrayList<>();
  }

  public Map<UUID, Double> requestCurrentPrices() {
    sendExtMsg(new RequestCurrentPrices());

    try {
      // blocks until actor puts something here
      ToExtEvSimDataMessage evMessage = receiveTriggerQueue.take();

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
  public void sendExtMsg(FromExtEvSimDataMessage msg) {
    evDataService.tell(msg, ActorRef.noSender());
    // todo: Why doesn't the ev data service schedule itself instead of having the sim adapter
    // overhead?
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(evDataService), ActorRef.noSender());
  }

  /**
   * @param extEvResponse
   */
  public void queueExtResponseMsg(ToExtEvSimDataMessage extEvResponse) {
    try {
      receiveTriggerQueue.put(extEvResponse);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
