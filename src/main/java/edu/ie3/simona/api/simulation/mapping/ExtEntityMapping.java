/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import java.util.*;

/** Contains the mapping between SIMONA uuid and external id and the data type the assets hold */
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
  public Map<String, UUID> getExtIdUuidMapping(String dataType) {
    Map<String, UUID> extIdUuidMapping = new HashMap<>();
    extEntities.forEach(
        ent -> {
          if (Objects.equals(ent.resultType(), dataType)) {
            extIdUuidMapping.put(ent.id(), ent.uuid());
          }
        });
    return extIdUuidMapping;
  }

  /**
   * Mapping SIMONA uuid to external id
   *
   * @param dataType data type the external asset expects
   * @return Mapping SIMONA uuid to external id
   */
  public Map<UUID, String> getExtUuidIdMapping(String dataType) {
    Map<UUID, String> extUuidIdMapping = new HashMap<>();
    extEntities.forEach(
        ent -> {
          if (Objects.equals(ent.resultType(), dataType)) {
            extUuidIdMapping.put(ent.uuid(), ent.id());
          }
        });
    return extUuidIdMapping;
  }
}
