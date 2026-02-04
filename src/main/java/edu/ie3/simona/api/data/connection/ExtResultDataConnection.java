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

/** Enables data transfer of results between SIMONA and simonaAPI. */
public final class ExtResultDataConnection
    extends BiDirectional<ResultDataMessageFromExt, ResultDataResponseMessageToExt> {

  /** A list of uuids for all registered result entities. */
  private final List<UUID> resultUuids;

  public ExtResultDataConnection(List<UUID> results) {
    this.resultUuids = results;
  }

  /**
   * Returns the uuids that are used by {@link #requestResults(long, boolean)}.
   */
  public List<UUID> getResultUuids() {
    return Collections.unmodifiableList(resultUuids);
  }
  

  /**
   * Method for requesting SIMONA results as a map uuid to object from an external simulation.
   *
   * @param tick For which results should be returned.
   * @return A map: uuid to results.
   * @throws InterruptedException - If the thread is interrupted while waiting for the results.
   */
  public Map<UUID, List<ResultEntity>> requestResults(long tick, boolean sendUnchangedResults) throws InterruptedException {
    return requestResults(tick, resultUuids, sendUnchangedResults);
  }

  /**
   * Method for requesting SIMONA results as a map uuid to object from an external simulation.
   *
   * @param tick For which results should be returned.
   * @param entities For with results should be returned.
   * @return A map: uuid to results.
   * @throws InterruptedException - If the thread is interrupted while waiting for the results.
   */
  public Map<UUID, List<ResultEntity>> requestResults(long tick, List<UUID> entities, boolean sendUnchangedResults)
      throws InterruptedException {
    sendExtMsg(new RequestResultEntities(tick, entities, sendUnchangedResults));
    return receiveWithType(ProvideResultEntities.class).results();
  }
}
