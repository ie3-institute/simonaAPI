/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.results;

import edu.ie3.datamodel.models.result.ResultEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Provides a list of results from SIMONA to an external simulation. */
public record ProvideResultEntities(Map<UUID, List<ResultEntity>> results)
    implements ResultDataResponseMessageToExt {

  public ProvideResultEntities(List<ResultEntity> resultEntities) {
    this(resultEntities.stream().collect(Collectors.groupingBy(ResultEntity::getInputModel)));
  }
}
