/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.system.SystemParticipantResult;
import edu.ie3.simona.api.data.ExtOutputDataConnection;
import edu.ie3.simona.api.data.ontology.DataMessageFromExt;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities;
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities;
import edu.ie3.simona.api.data.results.ontology.ResultDataMessageFromExt;
import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt;
import edu.ie3.simona.api.simulation.ontology.ControlResponseMessageFromExt;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.pekko.actor.typed.ActorRef;

/** Enables data connection of results between SIMONA and SimonaAPI */
public class ExtResultDataConnection implements ExtOutputDataConnection {

  /** Data message queue containing messages from SIMONA */
  public final LinkedBlockingQueue<ResultDataResponseMessageToExt> receiveTriggerQueue =
      new LinkedBlockingQueue<>();

  /** Actor reference to service that handles result data within SIMONA */
  private ActorRef<DataMessageFromExt> extResultDataService;

  /** Actor reference to the dataServiceAdapter */
  private ActorRef<DataMessageFromExt> dataServiceActivation;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef<ControlResponseMessageFromExt> extSimAdapter;

  /** Map uuid to external id of grid related entities */
  private final Map<UUID, String> gridResultAssetMapping;

  /** Map uuid to external id of system participants */
  private final Map<UUID, String> participantResultAssetMapping;

  public ExtResultDataConnection(
      Map<UUID, String> participantResultAssetMapping, Map<UUID, String> gridResultAssetMapping) {
    this.participantResultAssetMapping = participantResultAssetMapping;
    this.gridResultAssetMapping = gridResultAssetMapping;
  }

  /**
   * Sets the actor refs for data and control flow
   *
   * @param extResultDataService actor ref to the adapter of the data service for data messages
   * @param dataServiceActivation actor ref to the adapter of the data service for schedule
   *     activation messages
   * @param extSimAdapter actor ref to the extSimAdapter
   */
  public void setActorRefs(
      ActorRef<DataMessageFromExt> extResultDataService,
      ActorRef<DataMessageFromExt> dataServiceActivation,
      ActorRef<ControlResponseMessageFromExt> extSimAdapter) {
    this.extResultDataService = extResultDataService;
    this.dataServiceActivation = dataServiceActivation;
    this.extSimAdapter = extSimAdapter;
  }

  public List<UUID> getGridResultDataAssets() {
    return gridResultAssetMapping.keySet().stream().toList();
  }

  public List<UUID> getParticipantResultDataAssets() {
    return participantResultAssetMapping.keySet().stream().toList();
  }

  /** Method that an external simulation can request results from SIMONA as a list. */
  private List<ResultEntity> requestResultList(long tick) throws InterruptedException {
    sendExtMsg(new RequestResultEntities(tick));
    return receiveWithType(ProvideResultEntities.class).results();
  }

  /**
   * Method that an external simulation can request results from SIMONA as a map string to object.
   */
  public Map<String, ResultEntity> requestResults(long tick) throws InterruptedException {
    return createResultMap(requestResultList(tick));
  }

  protected Map<String, ResultEntity> createResultMap(List<ResultEntity> results) {
    Map<String, ResultEntity> resultMap = new HashMap<>();
    results.forEach(
        result -> {
          if (result instanceof NodeResult nodeResult) {
            resultMap.put(gridResultAssetMapping.get(nodeResult.getInputModel()), nodeResult);
          } else if (result instanceof SystemParticipantResult systemParticipantResult) {
            resultMap.put(
                participantResultAssetMapping.get(systemParticipantResult.getInputModel()),
                systemParticipantResult);
          } else {
            throw new IllegalArgumentException(
                "ExtResultData can only handle NodeResult's and SystemParticipantResult's!");
          }
        });
    return resultMap;
  }

  /**
   * Send information from the external simulation to SIMONA's external data service. Furthermore,
   * ExtSimAdapter within SIMONA is instructed to activate the external data service with the
   * current tick.
   *
   * @param msg the data/information that is sent to SIMONA's result data service
   */
  public void sendExtMsg(ResultDataMessageFromExt msg) {
    extResultDataService.tell(msg);
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataServiceActivation));
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
