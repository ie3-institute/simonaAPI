/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.simona.api.ontology.results.ProvideResultEntities;
import edu.ie3.simona.api.ontology.results.RequestResultEntities;
import edu.ie3.simona.api.ontology.results.ResultDataMessageFromExt;
import edu.ie3.simona.api.ontology.results.ResultDataResponseMessageToExt;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Enables data connection of results between SIMONA and SimonaAPI. */
public final class ExtResultDataConnection
    extends BiDirectional<ResultDataMessageFromExt, ResultDataResponseMessageToExt> {

  /** A list of uuids for all registered result entities. */
  private final List<UUID> resultUuids;

  public ExtResultDataConnection(List<UUID> results) {
    this.resultUuids = results;
  }

  public List<UUID> getResultUuids() {
    return Collections.unmodifiableList(resultUuids);
  }

  /** Method for requesting SIMONA results as list from an external simulation. */
  private List<ResultEntity> requestResultList(long tick) throws InterruptedException {
    return createResultList(requestResults(tick));
  }

  private List<ResultEntity> requestResultList(long tick, List<UUID> entities) throws InterruptedException {
    return createResultList(requestResults(tick, entities));
  }

  /** Method for requesting SIMONA results as a map uuid to object from an external simulation. */
  public Map<UUID, List<ResultEntity>> requestResults(long tick) throws InterruptedException {
    return requestResults(tick, resultUuids);
  }

  /** Method for requesting SIMONA results as a map uuid to object from an external simulation. */
  public Map<UUID, List<ResultEntity>> requestResults(long tick, List<UUID> entities)
      throws InterruptedException {
    sendExtMsg(new RequestResultEntities(tick, entities));
    return receiveWithType(ProvideResultEntities.class).results();
  }

  private List<ResultEntity> createResultList(Map<UUID, List<ResultEntity>> results) {
    return results.values().stream().flatMap(List::stream).toList();
  }
}
