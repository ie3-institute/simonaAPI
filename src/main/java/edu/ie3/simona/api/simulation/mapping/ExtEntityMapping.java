/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import java.util.*;
import java.util.stream.Collectors;

/** Contains the mapping between SIMONA uuid, the external id and the data type the assets hold */
public class ExtEntityMapping {

  private final Map<DataType, List<ExtEntityEntry>> extEntities;

  public ExtEntityMapping(List<ExtEntityEntry> extEntityEntryList) {
    this.extEntities =
        extEntityEntryList.stream().collect(Collectors.groupingBy(ExtEntityEntry::dataType));
  }

  /**
   * Mapping external id to SIMONA uuid
   *
   * @param dataType data type the external asset expects
   * @return Mapping external id to SIMONA uuid
   */
  public Map<String, UUID> getExtId2UuidMapping(DataType dataType) {
    return extEntities.getOrDefault(dataType, Collections.emptyList()).stream()
        .collect(Collectors.toMap(ExtEntityEntry::id, ExtEntityEntry::uuid));
  }

  /**
   * Mapping SIMONA uuid to external id
   *
   * @param dataType data type the external asset expects
   * @return Mapping SIMONA uuid to external id
   */
  public Map<UUID, String> getExtUuid2IdMapping(DataType dataType) {
    return extEntities.getOrDefault(dataType, Collections.emptyList()).stream()
        .collect(Collectors.toMap(ExtEntityEntry::uuid, ExtEntityEntry::id));
  }
}
