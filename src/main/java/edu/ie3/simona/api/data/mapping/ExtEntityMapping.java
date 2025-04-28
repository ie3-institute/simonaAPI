/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.mapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Contains the mapping between SIMONA uuid, the external id and the data type the assets hold */
public class ExtEntityMapping {

  private static final Logger log = LoggerFactory.getLogger(ExtEntityMapping.class);

  private final Map<DataType, List<ExtEntityEntry>> extEntities;

  public ExtEntityMapping(Map<DataType, List<ExtEntityEntry>> extEntities) {
    this.extEntities = extEntities;
  }

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
   * Mapping external id to SIMONA uuid
   *
   * @param dataType data type the external asset expects
   * @return Mapping external id to SIMONA uuid
   */
  public Map<String, UUID> getExtId2UuidMapping(DataType dataType) {
    return extEntities.getOrDefault(dataType, Collections.emptyList()).stream()
        .collect(Collectors.toMap(ExtEntityEntry::id, ExtEntityEntry::uuid));
  }

  public Map<String, UUID> getExtId2UuidMapping(DataType... dataType) {
    return Stream.of(dataType)
        .flatMap(type -> extEntities.getOrDefault(type, Collections.emptyList()).stream())
        .collect(Collectors.toMap(ExtEntityEntry::id, ExtEntityEntry::uuid));
  }

  public Map<String, UUID> getFullMapping() {
    return extEntities.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(ExtEntityEntry::id, ExtEntityEntry::uuid));
  }

  public Map<UUID, String> getFullMappingReverse() {
    return extEntities.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(ExtEntityEntry::uuid, ExtEntityEntry::id));
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

  public Map<UUID, String> getExtUuid2IdMapping(DataType... dataType) {
    return Stream.of(dataType)
        .flatMap(type -> extEntities.getOrDefault(type, Collections.emptyList()).stream())
        .collect(Collectors.toMap(ExtEntityEntry::uuid, ExtEntityEntry::id));
  }

  public static List<UUID> toSimona(List<String> list, Map<String, UUID> mapping) {
    return list.stream()
        .map(str -> Optional.ofNullable(mapping.get(str)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  public static List<String> toExt(List<UUID> list, Map<UUID, String> mapping) {
    return list.stream()
        .map(str -> Optional.ofNullable(mapping.get(str)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  /**
   * Maps a map: ids to value to a map: uuids to value.
   *
   * @param valueMap that should be remapped
   * @param mapping that is used
   * @return a map: uuids to value
   * @param <T> type of output values
   * @param <V> type of input values
   */
  @SuppressWarnings("unchecked")
  public static <T, V> Map<UUID, T> mapToSimona(
      Map<String, V> valueMap, Map<String, UUID> mapping) {
    Map<UUID, T> convertedMap = new HashMap<>();

    for (String key : valueMap.keySet()) {
      V value = valueMap.get(key);

      try {
        convertedMap.put(mapping.get(key), (T) value);
      } catch (ClassCastException e) {
        log.warn("Could not convert value {} for key {}", value, key, e);
      }
    }
    return convertedMap;
  }

  /**
   * Maps a map: uuids to value to a map: ids to value.
   *
   * @param valueMap that should be remapped
   * @param mapping that is used
   * @return a map: id to value
   * @param <T> type of output values
   * @param <V> type of input values
   */
  @SuppressWarnings("unchecked")
  public static <T, V> Map<String, T> mapToExt(Map<UUID, V> valueMap, Map<UUID, String> mapping) {
    Map<String, T> convertedMap = new HashMap<>();

    for (UUID key : valueMap.keySet()) {
      V value = valueMap.get(key);

      try {
        convertedMap.put(mapping.get(key), (T) value);
      } catch (ClassCastException e) {
        log.warn("Could not convert value {} for key {}", value, key, e);
      }
    }
    return convertedMap;
  }
}
