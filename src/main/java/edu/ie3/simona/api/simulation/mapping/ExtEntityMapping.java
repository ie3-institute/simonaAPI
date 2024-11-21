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

  private final List<ExtEntityEntry> extEntities;

  public ExtEntityMapping(List<ExtEntityEntry> extEntityEntryList) {
    this.extEntities = extEntityEntryList;
  }

  /**
   * Mapping external id to SIMONA uuid
   *
   * @param dataType data type the external asset expects
   * @return Mapping external id to SIMONA uuid
   */
  public Map<String, UUID> getExtId2UuidMapping(String dataType) {
    Map<String, UUID> extId2UuidMapping = new HashMap<>();
    extEntities.forEach(
        ent -> {
          if (Objects.equals(ent.resultType(), dataType)) {
            extId2UuidMapping.put(ent.id(), ent.uuid());
          }
        });
    return extId2UuidMapping;
  }

  /**
   * Mapping SIMONA uuid to external id
   *
   * @param dataType data type the external asset expects
   * @return Mapping SIMONA uuid to external id
   */
  public Map<UUID, String> getExtUuid2IdMapping(String dataType) {
    Map<UUID, String> extUuid2IdMapping = new HashMap<>();
    extEntities.forEach(
        ent -> {
          if (Objects.equals(ent.resultType(), dataType)) {
            extUuid2IdMapping.put(ent.uuid(), ent.id());
          }
        });
    return extUuid2IdMapping;
  }
}
