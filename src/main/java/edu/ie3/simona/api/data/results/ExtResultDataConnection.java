/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.simona.api.data.ExtOutputDataConnection;
import edu.ie3.simona.api.data.WithDataResponseToExt;
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage;
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities;
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities;
import edu.ie3.simona.api.data.results.ontology.ResultDataMessageFromExt;
import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt;
import org.apache.pekko.actor.ActorRef;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Enables data connection of results between SIMONA and SimonaAPI */
public class ExtResultDataConnection extends WithDataResponseToExt<ResultDataResponseMessageToExt>
    implements ExtOutputDataConnection<ResultDataMessageFromExt> {

  /** Actor reference to service that handles result data within SIMONA */
  private ActorRef extResultDataService;

  /** Actor reference to the dataServiceAdapter */
  private ActorRef dataServiceActivation;

  /** Actor reference to adapter that handles scheduler control flow in SIMONA */
  private ActorRef extSimAdapter;

  /** Map uuid to external id of grid related entities */
  private final List<UUID> gridResults;

  /** Map uuid to external id of system participants */
  private final List<UUID> participantResults;

  /** Map uuid to external id of participant flex options */
  private final List<UUID> flexResults;

  public ExtResultDataConnection(
      List<UUID> participantResults,
      List<UUID> gridResults,
      List<UUID> flexResults) {
    this.participantResults = participantResults;
    this.gridResults = gridResults;
    this.flexResults = flexResults;
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
      ActorRef extResultDataService, ActorRef dataServiceActivation, ActorRef extSimAdapter) {
    this.extResultDataService = extResultDataService;
    this.dataServiceActivation = dataServiceActivation;
    this.extSimAdapter = extSimAdapter;
  }

  public List<UUID> getGridResultDataAssets() {
    return gridResults;
  }

  public List<UUID> getParticipantResultDataAssets() {
    return participantResults;
  }

  public List<UUID> getFlexOptionAssets() {
    return flexResults;
  }

  /** Method that an external simulation can request results from SIMONA as a list. */
  private List<ResultEntity> requestResultList(long tick) throws InterruptedException {
    List<UUID> allExtEntities =
        Stream.concat(
                Stream.concat(getFlexOptionAssets().stream(), getGridResultDataAssets().stream()),
                getParticipantResultDataAssets().stream())
            .toList();
    sendExtMsg(new RequestResultEntities(tick, allExtEntities));
    return receiveWithType(ProvideResultEntities.class).results();
  }

  private List<ResultEntity> requestFlexOptionResultsList(long tick) throws InterruptedException {
    sendExtMsg(new RequestResultEntities(tick, getFlexOptionAssets()));
    return receiveWithType(ProvideResultEntities.class).results();
  }

  private List<ResultEntity> requestGridResultsList(long tick) throws InterruptedException {
    sendExtMsg(new RequestResultEntities(tick, getGridResultDataAssets()));
    return receiveWithType(ProvideResultEntities.class).results();
  }

  private List<ResultEntity> requestParticipantResultsList(long tick) throws InterruptedException {
    sendExtMsg(new RequestResultEntities(tick, getParticipantResultDataAssets()));
    return receiveWithType(ProvideResultEntities.class).results();
  }

  /**
   * Method that an external simulation can request results from SIMONA as a map string to object.
   */
  public Map<UUID, ResultEntity> requestResults(long tick) throws InterruptedException {
    return createResultMap(requestResultList(tick));
  }

  public Map<UUID, ResultEntity> requestFlexOptionResults(long tick) throws InterruptedException {
    return createResultMap(requestFlexOptionResultsList(tick));
  }

  public Map<UUID, ResultEntity> requestGridResults(long tick) throws InterruptedException {
    return createResultMap(requestGridResultsList(tick));
  }

  public Map<UUID, ResultEntity> requestParticipantResults(long tick)
      throws InterruptedException {
    return createResultMap(requestParticipantResultsList(tick));
  }

  protected Map<UUID, ResultEntity> createResultMap(List<ResultEntity> results) {
    return results.stream().collect(Collectors.toMap(ResultEntity::getInputModel, i -> i));
  }

  /**
   * Send information from the external simulation to SIMONA's external data service. Furthermore,
   * ExtSimAdapter within SIMONA is instructed to activate the external data service with the
   * current tick.
   *
   * @param msg the data/information that is sent to SIMONA's result data service
   */
  public void sendExtMsg(ResultDataMessageFromExt msg) {
    extResultDataService.tell(msg, ActorRef.noSender());
    // we need to schedule data receiver activation with scheduler
    extSimAdapter.tell(new ScheduleDataServiceMessage(dataServiceActivation), ActorRef.noSender());
  }
}
