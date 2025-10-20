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

  public ExtEntityMapping(List<ExtEntityEntry> entries) {
      entries.forEach(entry -> {
          DataType dataType = entry.dataType();
          UUID uuid = entry.uuid();
          String id = entry.id();

          entry.columnScheme().ifPresent(scheme -> primaryMapping.put(uuid, scheme.getValueClass()));

          // override mappings
          uuidToId.put(uuid, id);
          idToUuid.put(id, uuid);

          extAssets.computeIfAbsent(dataType, k -> new ArrayList<>()).add(uuid);
      });
  }

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

  private ExtEntityMapping(ExtEntityMapping mapping) {
      this.extAssets.putAll(mapping.extAssets);
      this.primaryMapping.putAll(mapping.primaryMapping);
      this.gridAssets.addAll(mapping.gridAssets);
      this.participants.addAll(mapping.participants);
      this.ems.addAll(mapping.ems);
      this.uuidToId.putAll(mapping.uuidToId);
      this.idToUuid.putAll(mapping.idToUuid);
  }

  // can override previous mappings
  public ExtEntityMapping include(List<ExtEntityEntry> included) {
      ExtEntityMapping copy = new ExtEntityMapping(this);
      copy.includeEntries(included);
      return copy;
  }

  public ExtEntityMapping include(DataType dataType, List<String> included, Optional<ColumnScheme> schemeOption) {
      ExtEntityMapping copy = new ExtEntityMapping(this);
      includeIds(dataType, included.stream().map(this::get).filter(Optional::isPresent).map(Optional::get).toList(), schemeOption);
      return copy;
  }

  private void includeIds(DataType dataType, List<UUID> included, Optional<ColumnScheme> schemeOption) {
      schemeOption.ifPresent(scheme -> included.forEach(uuid -> primaryMapping.put(uuid, scheme.getValueClass())));
      addExtEntities(dataType, included);
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

          addExtEntities(dataType, included.stream().map(ExtEntityEntry::uuid).toList());
      });
  }

  private void addExtEntities(DataType dataType, List<UUID> included) {
      if (dataType == DataType.PRIMARY_RESULT) {
          extAssets.computeIfAbsent(DataType.PRIMARY, d -> new ArrayList<>()).addAll(included);
          extAssets.computeIfAbsent(DataType.RESULT, d -> new ArrayList<>()).addAll(included);
      } else {
          extAssets.computeIfAbsent(dataType, k -> new ArrayList<>()).addAll(included);
      }
  }

  /** Returns the data types of this mapping. */
  public Set<DataType> getDataTypes() {
    return extAssets.keySet();
  }

    public Map<UUID, Class<? extends Value>> getPrimaryMapping() {
        return Collections.unmodifiableMap(primaryMapping);
    }

    public List<UUID> getAllAssets() {
      return extAssets.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  public List<UUID> getAssets(DataType dataType) {
      List<UUID> uuids = switch (dataType) {
          case PRIMARY, PRIMARY_RESULT -> new ArrayList<>(participants);
          case RESULT -> {
              List<UUID> res = new ArrayList<>();
              res.addAll(gridAssets);
              res.addAll(participants);
              res.addAll(ems);
              yield res;
          }
          case EM -> new ArrayList<>(ems);
          default -> Collections.emptyList();
      };

      if (uuids.isEmpty()) {
          if (dataType == DataType.PRIMARY_RESULT) {
              List<UUID> res = new ArrayList<>();
              res.addAll(extAssets.getOrDefault(DataType.PRIMARY, Collections.emptyList()));
              res.addAll(extAssets.getOrDefault(DataType.RESULT, Collections.emptyList()));

              return res;
          } else {
              return extAssets.getOrDefault(dataType, Collections.emptyList());
          }

      } else {
          List<UUID> ext = new  ArrayList<>();
          extAssets.values().forEach(ext::addAll);

          if (extAssets.isEmpty()) {
              return uuids;
          } else {
              return uuids.stream().filter(ext::contains).toList();
          }
      }
  }

  public boolean contains(String id) {
      return idToUuid.containsKey(id);
  }

  public boolean contains(UUID uuid) {
      return uuidToId.containsKey(uuid);
  }

  public UUID from(String id) {
      return idToUuid.get(id);
  }

  public String from(UUID uuid) {
      return uuidToId.get(uuid);
  }

  public Optional<UUID> get(String id) {
      return Optional.ofNullable(idToUuid.get(id));
  }

  public Optional<String> get(UUID uuid) {
      return Optional.ofNullable(uuidToId.get(uuid));
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
}
