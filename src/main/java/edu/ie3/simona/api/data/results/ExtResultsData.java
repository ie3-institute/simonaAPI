/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.connector.ConnectorResult;
import edu.ie3.datamodel.models.result.system.ElectricalEnergyStorageResult;
import edu.ie3.datamodel.models.result.system.SystemParticipantResult;
import edu.ie3.datamodel.models.result.system.SystemParticipantWithHeatResult;
import edu.ie3.datamodel.models.result.thermal.ThermalUnitResult;
import edu.ie3.simona.api.data.ExtData;
import edu.ie3.simona.api.data.ontology.DataResponseMessageToExt;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities;
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities;
import edu.ie3.simona.api.data.results.ontology.ResultDataMessageFromExt;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.pekko.actor.ActorRef;

public class ExtResultsData implements ExtData {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<DataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  /** Actor reference to service that handles ev data within SIMONA */
  private final ActorRef dataService;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private final ActorRef extSimAdapter;

  // important trigger queue must be the same as hold in actor
  // to make it safer one might consider asking the actor for ara reference on its trigger queue?!
  public ExtResultsData(ActorRef dataService, ActorRef extSimAdapter) {
    this.dataService = dataService;
    this.extSimAdapter = extSimAdapter;
  }

  /** Method that an external simulation can request results from SIMONA as a list. */
  public List<ResultEntity> requestResults() throws InterruptedException {
    sendExtMsg(new RequestResultEntities());
    return receiveWithType(ProvideResultEntities.class).results();
  }

  /**
   * Method that an external simulation can request results from SIMONA as a map string to object.
   */
  public Map<String, Object> requestResultObjects() throws RuntimeException, InterruptedException {
    return convertResultsList(requestResults());
  }

  protected Map<String, Object> convertResultsList(List<ResultEntity> results)
      throws RuntimeException {
    Map<String, Object> resultsMap = new HashMap<>();
    String oneResult;
    for (ResultEntity res : results) {
      if (res instanceof SystemParticipantWithHeatResult systemParticipantWithHeatResult) {
        oneResult =
            "{\"p\":\""
                + systemParticipantWithHeatResult.getP()
                + ",\"q\":\""
                + systemParticipantWithHeatResult.getQ()
                + ",\"qDot\":\""
                + systemParticipantWithHeatResult.getqDot()
                + "\"}";
      } else if (res instanceof ElectricalEnergyStorageResult electricalEnergyStorageResult) {
        oneResult =
            "{\"p\":\""
                + electricalEnergyStorageResult.getP()
                + ",\"q\":\""
                + electricalEnergyStorageResult.getQ()
                + ",\"soc\":\""
                + electricalEnergyStorageResult.getSoc()
                + "\"}";
      } else if (res instanceof ConnectorResult connectorResult) {
        oneResult =
            "{\"iAMag\":\""
                + connectorResult.getiAMag()
                + ",\"iAAng\":\""
                + connectorResult.getiAAng()
                + ",\"iBMag\":\""
                + connectorResult.getiBMag()
                + ",\"iBAng\":\""
                + connectorResult.getiBAng()
                + "\"}";
      } else if (res instanceof NodeResult nodeResult) {
        oneResult =
            "{\"vMag\":\"" + nodeResult.getvMag() + ",\"vAng\":\"" + nodeResult.getvAng() + "\"}";
      } else if (res instanceof ThermalUnitResult thermalUnitResult) {
        oneResult = "{\"qDot\":\"" + thermalUnitResult.getqDot() + "\"}";
      } else if (res instanceof SystemParticipantResult systemParticipantResult) {
        oneResult =
            "{\"p\":\""
                + systemParticipantResult.getP()
                + ",\"q\":\""
                + systemParticipantResult.getQ()
                + "\"}";
      } else {
        oneResult = "{}";
      }
      resultsMap.put(res.getUuid().toString(), oneResult);
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
  public void queueExtResponseMsg(DataResponseMessageToExt msg) throws InterruptedException {
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
  private <T extends DataResponseMessageToExt> T receiveWithType(Class<T> expectedMessageClass)
      throws InterruptedException {

    // blocks until actor puts something here
    DataResponseMessageToExt msg = receiveTriggerQueue.take();

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
