/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ev.model.EvModel;
import edu.ie3.simona.api.data.ev.ontology.ProvideArrivingEvs;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.results.ontology.*;
import edu.ie3.simona.api.exceptions.ConvertionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.pekko.actor.ActorRef;

public class ExtResultData implements ExtData {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<ResultDataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  /** Actor reference to service that handles ev data within SIMONA */
  private final ActorRef dataService;

  private final ActorRef dataServiceActivation;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private final ActorRef extSimAdapter;

  private final ResultDataFactory factory;

  private final List<UUID> resultDataAssets;

  public ExtResultData(ActorRef dataService, ActorRef dataServiceActivation, ActorRef extSimAdapter, ResultDataFactory factory, List<UUID> resultDataAssets) {
    this.dataService = dataService;
    this.dataServiceActivation = dataServiceActivation;
    this.extSimAdapter = extSimAdapter;
    this.factory = factory;
    this.resultDataAssets = resultDataAssets;
  }

  public List<UUID> getResultDataAssets() {
    return resultDataAssets;
  }

  /** Method that an external simulation can request results from SIMONA as a list. */
  public List<ResultEntity> requestResults(long tick) throws InterruptedException {
    sendExtMsg(new RequestResultEntities(tick));
    return receiveWithType(ProvideResultEntities.class).results();
  }

  /**
   * Method that an external simulation can request results from SIMONA as a map string to object.
   */
  public Map<String, Object> requestResultObjects(long tick)
      throws ConvertionException, InterruptedException {
    return convertResultsList(requestResults(tick));
  }

  protected Map<String, Object> convertResultsList(List<ResultEntity> results)
      throws ConvertionException {
    Map<String, Object> resultsMap = new HashMap<>();
    Object convertedResult;
    for (ResultEntity res : results) {
      convertedResult = factory.convert(res);
      resultsMap.put(res.getInputModel().toString(), convertedResult);
    }
    return resultsMap;
  }

  /**
   * Send information from the external simulation to SIMONA's external data service. Furthermore,
   * ExtSimAdapter within SIMONA is instructed to activate the external data service with the
   * current tick.
   *
   * @param msg the data/information that is sent to SIMONA's result data service
   */
  public void sendExtMsg(ResultDataMessageFromExt msg) {
    dataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataServiceActivation), ActorRef.noSender());
  }

  /** Queues message from SIMONA that should be handled by the external simulation. */
  public void queueExtResponseMsg(ResultDataResponseMessageToExt msg) throws InterruptedException {
    receiveTriggerQueue.put(msg);
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
  private <T extends ResultDataResponseMessageToExt> T receiveWithType(
      Class<T> expectedMessageClass) throws InterruptedException {

    // blocks until actor puts something here
    ResultDataResponseMessageToExt msg = receiveTriggerQueue.take();

    if (msg.getClass().equals(expectedMessageClass)) {
      return (T) msg;
    } else
      throw new RuntimeException(
          "Received unexpected message '"
              + msg
              + "', expected type '"
              + expectedMessageClass
              + "'");
  }
}
