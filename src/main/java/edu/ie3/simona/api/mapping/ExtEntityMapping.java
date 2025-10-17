/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.mapping;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.input.container.GridContainer;
import edu.ie3.datamodel.models.value.Value;

import java.util.*;
import java.util.stream.Collectors;

/** Contains the mapping between SIMONA uuid, the external id and the data type the assets hold */
public class ExtEntityMapping {

  private final Map<DataType, List<UUID>> extAssets = new HashMap<>();
  private final Map<UUID, Class<? extends Value>> primaryMapping = new HashMap<>();

  // asset lists
  private final Set<UUID> gridAssets = new HashSet<>();
  private final Set<UUID> participants = new HashSet<>();
  private final Set<UUID> ems = new HashSet<>();

  private final Map<UUID, String> uuidToId = new HashMap<>();
  private final Map<String, UUID> idToUuid = new HashMap<>();

  public ExtEntityMapping(GridContainer grid) {
      // handling of grid assets
      grid.getRawGrid().allEntitiesAsList().forEach(asset -> {
          UUID uuid = asset.getUuid();
          String id = asset.getId();

          // add to mappings
          uuidToId.put(uuid, id);
          idToUuid.put(id, uuid);

          // add to asset list
          gridAssets.add(asset.getUuid());
      });

      // handling of participants and ems
      grid.getSystemParticipants().allEntitiesAsList().forEach(participant -> {
          UUID uuid = participant.getUuid();
          String id = participant.getId();

          // add to mappings
          uuidToId.put(uuid, id);
          idToUuid.put(id, uuid);

          // add to asset list
          participants.add(participant.getUuid());

          // add ems
          participant.getControllingEm().ifPresent(em -> {
              UUID emUuid = em.getUuid();
              String emId = em.getId();

              // add to mappings
              uuidToId.put(emUuid, emId);
              idToUuid.put(emId, emUuid);

              // add to asset list
              ems.add(emUuid);
          });
      });
  }

  private ExtEntityMapping(
          Map<DataType, List<UUID>> assets,
          Map<UUID, Class<? extends Value>> primaryMapping,
           Set<UUID> gridAssets,
    Set<UUID> participants,
    Set<UUID> ems,
          Map<UUID, String> uuidToId,
          Map<String, UUID> idToUUID
  ) {
      this.extAssets.putAll(assets);
      this.primaryMapping.putAll(primaryMapping);
      this.gridAssets.addAll(gridAssets);
      this.participants.addAll(participants);
      this.ems.addAll(ems);
      this.uuidToId.putAll(uuidToId);
      this.idToUuid.putAll(idToUUID);
  }

  // can override previous mappings
  public ExtEntityMapping include(List<ExtEntityEntry> included) {
      ExtEntityMapping copy = new ExtEntityMapping(extAssets, primaryMapping, gridAssets, participants, ems, uuidToId, idToUuid);
      copy.includeEntries(included);
      return copy;
  }

  public ExtEntityMapping include(DataType dataType, List<String> included, Optional<ColumnScheme> schemeOption) {
      ExtEntityMapping copy = new ExtEntityMapping(extAssets, primaryMapping, gridAssets, participants, ems, uuidToId, idToUuid);
      includeIds(dataType, included.stream().map(this::from).toList(), schemeOption);
      return copy;
  }

  private void includeIds(DataType dataType, List<UUID> included, Optional<ColumnScheme> schemeOption) {
      schemeOption.ifPresent(scheme -> included.forEach(uuid -> primaryMapping.put(uuid, scheme.getValueClass())));
      included.forEach(uuid -> extAssets.computeIfAbsent(dataType, d -> new ArrayList<>()).add(uuid));
  }

    // overrides previous mappings
  private void includeEntries(List<ExtEntityEntry> included) {
      included.forEach(entry -> {
          DataType dataType = entry.dataType();
          UUID uuid = entry.uuid();
          String id = entry.id();

          entry.columnScheme().ifPresent(scheme -> primaryMapping.put(uuid, scheme.getValueClass()));

          // override mappings
          uuidToId.put(uuid, id);
          idToUuid.put(id, uuid);

          extAssets.computeIfAbsent(dataType, d -> new ArrayList<>()).add(uuid);
      });
  }


  /** Returns the data types of this mapping. */
  public Set<DataType> getDataTypes() {
    return extAssets.keySet();
  }

    public Map<UUID, Class<? extends Value>> getPrimaryMapping() {
        return Collections.unmodifiableMap(primaryMapping);
    }

    public List<UUID> getExtAssets() {
      return extAssets.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  public List<UUID> getAssets(DataType dataType) {
      List<UUID> uuids = extAssets.getOrDefault(dataType, Collections.emptyList());

      return switch (dataType) {
          case PRIMARY, PRIMARY_RESULT -> new ArrayList<>(participants);
          case RESULT -> {
              List<UUID> res = new ArrayList<>();
              res.addAll(gridAssets);
              res.addAll(participants);
              res.addAll(ems);
              yield res;
          }
          case EM -> new ArrayList<>(ems);
          default -> uuids;
      };
  }

  public UUID from(String id) {
      return idToUuid.get(id);
  }

  public String from(UUID uuid) {
      return uuidToId.get(uuid);
  }

  /**
   * Returns the full mapping external id to SIMONA uuid. Equals {@code
   * getExtId2UuidMapping(DataType.values())}.
   */
  public Map<String, UUID> getExtId2UuidMapping() {
    return Collections.unmodifiableMap(idToUuid);
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
  @Deprecated
  public Map<String, UUID> getExtId2UuidMapping(DataType dataType) {
      List<UUID> uuids = extAssets.getOrDefault(dataType, Collections.emptyList());
      return getExtId2UuidMapping(uuids);
  }

  /**
   * Mapping external id to SIMONA uuid.
   *
   * @param dataTypes the external asset expects
   * @return mapping external id to SIMONA uuid
   */
  @Deprecated
  public Map<String, UUID> getExtId2UuidMapping(DataType... dataTypes) {
      List<UUID> uuids = getExtAssets();
      return getExtId2UuidMapping(uuids);
  }

  /**
   * Mapping SIMONA uuid to external id.
   *
   * @param dataType data type the external asset expects
   * @return mapping SIMONA uuid to external id
   */
  @Deprecated
  public Map<UUID, String> getExtUuid2IdMapping(DataType dataType) {
      List<UUID> uuids = extAssets.getOrDefault(dataType, Collections.emptyList());
    return getExtUuid2IdMapping(uuids);
  }

  /**
   * Mapping SIMONA uuid to external id.
   *
   * @param dataTypes data types the external asset expects
   * @return mapping SIMONA uuid to external id
   */
  @Deprecated
  public Map<UUID, String> getExtUuid2IdMapping(DataType... dataTypes) {
      List<UUID> uuids = getExtAssets();
      return getExtUuid2IdMapping(uuids);
  }

  private Map<String, UUID> getExtId2UuidMapping(List<UUID> uuids) {
      if (uuids.isEmpty()) {
          return Collections.emptyMap();
      } else {
          return idToUuid.entrySet().stream().filter(entry -> uuids.contains(entry.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
