/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.mapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Contains the mapping between SIMONA uuid, the external id and the data type the assets hold */
public class ExtEntityMapping {

  private final Map<DataType, List<ExtEntityEntry>> extEntities;

  public ExtEntityMapping(List<ExtEntityEntry> extEntityEntryList) {
    this.extEntities =
        extEntityEntryList.stream().collect(Collectors.groupingBy(ExtEntityEntry::dataType));
  }

  /** Returns the data types of this mapping. */
  public Set<DataType> getDataTypes() {
    return extEntities.keySet();
  }

  /**
   * Method for getting the external entity entries for a specific data type.
   *
   * @param dataType for which entries should be returned
   * @return a list containing all entries or an empty list
   */
  public List<ExtEntityEntry> getEntries(DataType dataType) {
    return extEntities.getOrDefault(dataType, Collections.emptyList());
  }

  /**
   * Returns the full mapping external id to SIMONA uuid. Equals {@code
   * getExtId2UuidMapping(DataType.values())}.
   */
  public Map<String, UUID> getExtId2UuidMapping() {
    return extEntities.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(ExtEntityEntry::id, ExtEntityEntry::uuid));
  }

  /**
   * Returns the full mapping SIMONA uuid to external id. Equals {@code
   * getExtUuid2IdMapping(DataType.values())}.
   */
  public Map<UUID, String> getExtUuid2IdMapping() {
    return extEntities.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(ExtEntityEntry::uuid, ExtEntityEntry::id));
  }

  /**
   * Mapping external id to SIMONA uuid.
   *
   * @param dataType data type the external asset expects
   * @return mapping external id to SIMONA uuid
   */
  public Map<String, UUID> getExtId2UuidMapping(DataType dataType) {
    return extEntities.getOrDefault(dataType, Collections.emptyList()).stream()
        .collect(Collectors.toMap(ExtEntityEntry::id, ExtEntityEntry::uuid));
  }

  /**
   * Mapping external id to SIMONA uuid.
   *
   * @param dataTypes the external asset expects
   * @return mapping external id to SIMONA uuid
   */
  public Map<String, UUID> getExtId2UuidMapping(DataType... dataTypes) {
    return Stream.of(dataTypes)
        .flatMap(type -> extEntities.getOrDefault(type, Collections.emptyList()).stream())
        .collect(Collectors.toMap(ExtEntityEntry::id, ExtEntityEntry::uuid));
  }

  /**
   * Mapping SIMONA uuid to external id.
   *
   * @param dataType data type the external asset expects
   * @return mapping SIMONA uuid to external id
   */
  public Map<UUID, String> getExtUuid2IdMapping(DataType dataType) {
    return extEntities.getOrDefault(dataType, Collections.emptyList()).stream()
        .collect(Collectors.toMap(ExtEntityEntry::uuid, ExtEntityEntry::id));
  }

  /**
   * Mapping SIMONA uuid to external id.
   *
   * @param dataTypes data types the external asset expects
   * @return mapping SIMONA uuid to external id
   */
  public Map<UUID, String> getExtUuid2IdMapping(DataType... dataTypes) {
    return Stream.of(dataTypes)
        .flatMap(type -> extEntities.getOrDefault(type, Collections.emptyList()).stream())
        .collect(Collectors.toMap(ExtEntityEntry::uuid, ExtEntityEntry::id));
  }
}
