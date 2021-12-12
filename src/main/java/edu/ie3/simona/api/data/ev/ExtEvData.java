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
  public final LinkedBlockingQueue<ExtEvResponseMessage> receiveTriggerQueue =
      new LinkedBlockingQueue<>();
  private final ActorRef dataService;
  private final ActorRef extSimAdapter;

  // important trigger queue must be the same as hold in actor
  // to make it safer one might consider asking the actor for ara reference on its trigger queue?!
  public ExtEvData(ActorRef dataService, ActorRef extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

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

  public List<EvModel> sendEvPositions(EvMovementsMessage evMovementsMessage) {
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
