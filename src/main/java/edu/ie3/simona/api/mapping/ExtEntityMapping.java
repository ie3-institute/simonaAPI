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

  private final Map<DataType, List<UUID>> assets = new HashMap<>();
  private final Map<UUID, String> uuidToId = new HashMap<>();
  private final Map<String, UUID> idToUUID = new HashMap<>();

  public ExtEntityMapping(List<ExtEntityEntry> extEntityEntryList) {
    this.extEntities = extEntityEntryList.stream().collect(Collectors.groupingBy(ExtEntityEntry::dataType));

    // handling of general mapping (from grid container)
    List<UUID> simonaUUIDs = new ArrayList<>();
    assets.put(DataType.GENERAL, simonaUUIDs);

    extEntities.getOrDefault(DataType.GENERAL, Collections.emptyList()).forEach(entry -> {
        UUID uuid = entry.uuid();
        String id = entry.id();

        idToUUID.put(id, uuid);
        uuidToId.put(uuid, id);
        simonaUUIDs.add(uuid);
    });

    // handling of mapping from external simulation
    for (DataType dataType : DataType.getExceptGeneral()) {
        if (extEntities.containsKey(dataType)) {
            List<ExtEntityEntry> entries = extEntities.get(dataType);

            List<UUID> uuids = new ArrayList<>();

            entries.forEach(entry -> {
                UUID uuid = entry.uuid();
                String id = entry.id();

                idToUUID.put(id, uuid);
                uuidToId.put(uuid, id);
                uuids.add(uuid);
            });

            assets.put(dataType, uuids);
        }
    }
  }

  // can override previous mappings
  public ExtEntityMapping updateWith(List<ExtEntityEntry> additional) {
      List<ExtEntityEntry> entries = allEntries();
      entries.addAll(additional);
      return new ExtEntityMapping(entries);
  }

  /** Returns the data types of this mapping. */
  public Set<DataType> getDataTypes() {
    return assets.keySet();
  }

  public List<UUID> getAssets() {
      return assets.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  public List<UUID> getAssets(DataType dataType) {
      return assets.getOrDefault(dataType, Collections.emptyList());
  }

  public List<UUID> getAssets(DataType... dataTypes) {
    return Stream.of(dataTypes).flatMap(type -> assets.getOrDefault(type, Collections.emptyList()).stream()).toList();
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

  public List<ExtEntityEntry> allEntries() {
      List<ExtEntityEntry> entries = new ArrayList<>();
      extEntities.values().forEach(entries::addAll);
    return entries;
  }

  /**
   * Returns the full mapping external id to SIMONA uuid. Equals {@code
   * getExtId2UuidMapping(DataType.values())}.
   */
  public Map<String, UUID> getExtId2UuidMapping() {
    return Collections.unmodifiableMap(idToUUID);
  }

  /**
   * Returns the full mapping SIMONA uuid to external id. Equals {@code
   * getExtUuid2IdMapping(DataType.values())}.
   */
  public Map<UUID, String> getExtUuid2IdMapping() {
    return Collections.unmodifiableMap(uuidToId);
  }

  /**
   * Mapping external id to SIMONA uuid.
   *
   * @param dataType data type the external asset expects
   * @return mapping external id to SIMONA uuid
   */
  public Map<String, UUID> getExtId2UuidMapping(DataType dataType) {
      List<UUID> uuids = assets.getOrDefault(dataType, Collections.emptyList());
      return getExtId2UuidMapping(uuids);
  }

  /**
   * Mapping external id to SIMONA uuid.
   *
   * @param dataTypes the external asset expects
   * @return mapping external id to SIMONA uuid
   */
  public Map<String, UUID> getExtId2UuidMapping(DataType... dataTypes) {
      List<UUID> uuids = getAssets(dataTypes);
      return getExtId2UuidMapping(uuids);
  }

  /**
   * Mapping SIMONA uuid to external id.
   *
   * @param dataType data type the external asset expects
   * @return mapping SIMONA uuid to external id
   */
  public Map<UUID, String> getExtUuid2IdMapping(DataType dataType) {
      List<UUID> uuids = assets.getOrDefault(dataType, Collections.emptyList());
    return getExtUuid2IdMapping(uuids);
  }

  /**
   * Mapping SIMONA uuid to external id.
   *
   * @param dataTypes data types the external asset expects
   * @return mapping SIMONA uuid to external id
   */
  public Map<UUID, String> getExtUuid2IdMapping(DataType... dataTypes) {
      List<UUID> uuids = getAssets(dataTypes);
      return getExtUuid2IdMapping(uuids);
  }

  private Map<String, UUID> getExtId2UuidMapping(List<UUID> uuids) {
      if (uuids.isEmpty()) {
          return Collections.emptyMap();
      } else {
          return idToUUID.entrySet().stream().filter(entry -> uuids.contains(entry.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      }
  }

    private Map<UUID, String> getExtUuid2IdMapping(List<UUID> uuids) {
        if (uuids.isEmpty()) {
            return Collections.emptyMap();
        } else {
            return uuidToId.entrySet().stream().filter(entry -> uuids.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }
}
