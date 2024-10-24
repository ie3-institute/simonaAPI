/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities;
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities;
import edu.ie3.simona.api.data.results.ontology.ResultDataMessageFromExt;
import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt;
import edu.ie3.simona.api.exceptions.ConvertionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.pekko.actor.ActorRef;

public class ExtResultData implements ExtData {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<ResultDataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  /** Actor reference to service that handles ev data within SIMONA */
  private final ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private final ActorRef extSimAdapter;

  private final ResultDataFactory factory;

  public ExtResultData(ActorRef dataService, ActorRef extSimAdapter, ResultDataFactory factory) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
    this.factory = factory;
  }

  /** Method that an external simulation can request results from SIMONA as a list. */
  public List<ModelResultEntity> requestResults() throws InterruptedException {
    sendExtMsg(new RequestResultEntities());
    return receiveWithType(ProvideResultEntities.class).results();
  }

  /**
   * Method that an external simulation can request results from SIMONA as a map string to object.
   */
  public Map<String, Object> requestResultObjects()
      throws ConvertionException, InterruptedException {
    return convertResultsList(requestResults());
  }

  protected Map<String, Object> convertResultsList(List<ModelResultEntity> results)
      throws ConvertionException {
    Map<String, Object> resultsMap = new HashMap<>();
    Object convertedResult;
    for (ModelResultEntity res : results) {
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
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataService), ActorRef.noSender());
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
