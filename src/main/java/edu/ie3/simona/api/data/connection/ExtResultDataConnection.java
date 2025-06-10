/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities;
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities;
import edu.ie3.simona.api.data.results.ontology.ResultDataMessageFromExt;
import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Enables data connection of results between SIMONA and SimonaAPI. */
public final class ExtResultDataConnection
    extends BiDirectional<ResultDataMessageFromExt, ResultDataResponseMessageToExt> {

  /** Map uuid to external id of grid related entities. */
  private final List<UUID> gridResults;

  /** Map uuid to external id of system participants. */
  private final List<UUID> participantResults;

  /** Map uuid to external id of participant flex options. */
  private final List<UUID> flexResults;

  public ExtResultDataConnection(
      List<UUID> participantResults, List<UUID> gridResults, List<UUID> flexResults) {
    this.participantResults = participantResults;
    this.gridResults = gridResults;
    this.flexResults = flexResults;
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

  /** Method for requesting SIMONA results as list from an external simulation. */
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

  /** Method for requesting SIMONA results as a map uuid to object from an external simulation. */
  public Map<UUID, ResultEntity> requestResults(long tick) throws InterruptedException {
    return createResultMap(requestResultList(tick));
  }

  public Map<UUID, ResultEntity> requestFlexOptionResults(long tick) throws InterruptedException {
    return createResultMap(requestFlexOptionResultsList(tick));
  }

  public Map<UUID, ResultEntity> requestGridResults(long tick) throws InterruptedException {
    return createResultMap(requestGridResultsList(tick));
  }

  public Map<UUID, ResultEntity> requestParticipantResults(long tick) throws InterruptedException {
    return createResultMap(requestParticipantResultsList(tick));
  }

  private Map<UUID, ResultEntity> createResultMap(List<ResultEntity> results) {
    return results.stream().collect(Collectors.toMap(ResultEntity::getInputModel, i -> i));
  }
}
