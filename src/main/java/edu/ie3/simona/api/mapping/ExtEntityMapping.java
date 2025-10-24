/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.mapping;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.container.GridContainer;
import edu.ie3.datamodel.models.value.Value;

import java.util.*;

/** Contains the mapping between SIMONA uuid, the external id and the data type the assets hold */
public class ExtEntityMapping {

  private final EnumMap<DataType, Set<UUID>> extAssets = new EnumMap<>(DataType.class);
  private final Map<UUID, Class<? extends Value>> primaryMapping = new HashMap<>();

  // asset lists
  private final Set<UUID> gridAssets = new HashSet<>();
  private final Set<UUID> participants = new HashSet<>();
  private final Set<UUID> ems = new HashSet<>();

  private final Map<UUID, String> uuidToId = new HashMap<>();
  private final Map<String, UUID> idToUuid = new HashMap<>();

  public ExtEntityMapping(List<ExtEntityEntry> entries) {
    entries.forEach(
        entry -> {
          DataType dataType = entry.dataType();
          UUID uuid = entry.uuid();
          String id = entry.id();

          entry
              .columnScheme()
              .ifPresent(scheme -> primaryMapping.put(uuid, scheme.getValueClass()));

          // override mappings
          uuidToId.put(uuid, id);
          idToUuid.put(id, uuid);

          extAssets.computeIfAbsent(dataType, k -> new HashSet<>()).add(uuid);
        });
  }

  /**
   * Creates a mapping based on the uuids and ids found in the provided grid.
   *
   * @param grid that contains some mapping information
   */
  public ExtEntityMapping(GridContainer grid) {
    // handling of grid assets
    grid.getRawGrid()
        .allEntitiesAsList()
        .forEach(
            asset -> {
              UUID uuid = asset.getUuid();
              String id = asset.getId();

              // add to mappings
              uuidToId.put(uuid, id);
              idToUuid.put(id, uuid);

              // add to asset list
              gridAssets.add(asset.getUuid());
            });

    Set<EmInput> emInputs = new HashSet<>();

    // handling of participants and ems
    grid.getSystemParticipants()
        .allEntitiesAsList()
        .forEach(
            participant -> {
              UUID uuid = participant.getUuid();
              String id = participant.getId();

              // add to mappings
              uuidToId.put(uuid, id);
              idToUuid.put(id, uuid);

              // add to asset list
              participants.add(participant.getUuid());

              // add ems
              participant
                  .getControllingEm()
                  .ifPresent(
                      em -> {
                        UUID emUuid = em.getUuid();
                        String emId = em.getId();

                        // add to mappings
                        uuidToId.put(emUuid, emId);
                        idToUuid.put(emId, emUuid);

                        // add to asset list
                        ems.add(emUuid);
                        emInputs.add(em);
                      });
            });


      handleEMs(emInputs).forEach(em -> {
          UUID uuid = em.getUuid();
          String id = em.getId();

          uuidToId.put(uuid, id);
          idToUuid.put(id, uuid);

          ems.add(uuid);
      });
  }

  private Set<EmInput> handleEMs(Set<EmInput> givenSet) {
      Set<EmInput> next = new HashSet<>();

      givenSet.forEach(em -> em.getControllingEm().ifPresent(next::add));

      Set<EmInput> result = new HashSet<>(givenSet);

      if (!next.isEmpty()) {
          result.addAll(handleEMs(next));
      }

      return result;
  }

  /**
   * Used to copy another mapping.
   *
   * @param mapping that should be copied
   */
  private ExtEntityMapping(ExtEntityMapping mapping) {
    this.extAssets.putAll(mapping.extAssets);
    this.primaryMapping.putAll(mapping.primaryMapping);
    this.gridAssets.addAll(mapping.gridAssets);
    this.participants.addAll(mapping.participants);
    this.ems.addAll(mapping.ems);
    this.uuidToId.putAll(mapping.uuidToId);
    this.idToUuid.putAll(mapping.idToUuid);
  }

  /**
   * Method to add entities that should be used for the data connection.
   *
   * <p>Note: This method can override the existing uuid-id mapping!
   *
   * @param included a list of entries
   * @return an updated mapping
   */
  public ExtEntityMapping include(List<ExtEntityEntry> included) {
    ExtEntityMapping copy = new ExtEntityMapping(this);
    copy.includeEntries(included);
    return copy;
  }

  /**
   * Method to specify entities that should be used for the data connection. The ids are matched
   * against the uuid-id mapping that is already know.
   *
   * @param included a list of ids
   * @return an updated mapping
   */
  public ExtEntityMapping include(
      DataType dataType, List<String> included, Optional<ColumnScheme> schemeOption) {
    ExtEntityMapping copy = new ExtEntityMapping(this);
    List<UUID> includedUuids =
        included.stream().map(this::get).filter(Optional::isPresent).map(Optional::get).toList();

    schemeOption.ifPresent(
        scheme -> includedUuids.forEach(uuid -> primaryMapping.put(uuid, scheme.getValueClass())));
    addExtEntities(dataType, includedUuids);

    return copy;
  }

  protected void includeIds(
      DataType dataType, List<UUID> included, Optional<ColumnScheme> schemeOption) {
    schemeOption.ifPresent(
        scheme -> included.forEach(uuid -> primaryMapping.put(uuid, scheme.getValueClass())));
    addExtEntities(dataType, included);
  }

  /**
   * Method to add entities that should be used for the data connection.
   *
   * <p>Note: This method can override the existing uuid-id mapping!
   *
   * @param included a list of entries
   */
  private void includeEntries(List<ExtEntityEntry> included) {
    included.forEach(
        entry -> {
          DataType dataType = entry.dataType();
          UUID uuid = entry.uuid();
          String id = entry.id();

          entry
              .columnScheme()
              .ifPresent(scheme -> primaryMapping.put(uuid, scheme.getValueClass()));

          // override mappings
          uuidToId.put(uuid, id);
          idToUuid.put(id, uuid);

          addExtEntities(dataType, included.stream().map(ExtEntityEntry::uuid).toList());
        });
  }

  /**
   * Method to handle adding the entities to the {@link #extAssets} map.
   *
   * @param dataType for which assets are added
   * @param included a list of included uuids
   */
  private void addExtEntities(DataType dataType, List<UUID> included) {
    if (dataType == DataType.PRIMARY_RESULT) {
      extAssets.computeIfAbsent(DataType.PRIMARY, d -> new HashSet<>()).addAll(included);
      extAssets.computeIfAbsent(DataType.RESULT, d -> new HashSet<>()).addAll(included);
    } else {
      extAssets.computeIfAbsent(dataType, k -> new HashSet<>()).addAll(included);
    }
  }

  /** Returns the data types of this mapping. */
  public Set<DataType> getDataTypes() {
    return extAssets.keySet();
  }

  /** Returns a map: uuid to primary data class. */
  public Map<UUID, Class<? extends Value>> getPrimaryMapping() {
    return Collections.unmodifiableMap(primaryMapping);
  }

  /** Returns a list of all external assets. */
  public List<UUID> getAllAssets() {
    return extAssets.values().stream().flatMap(Collection::stream).toList();
  }

  /**
   * Method to return all assets of a specific data type. If no external assets are set, all assets
   * that are provided by a grid are used. In case external entities are present, they will be used
   * to filter the grid assets. If no grid assets are present, only the external assets will be
   * returned.
   *
   * @param dataType for which assets should be returned
   * @return a list of uuids
   */
  public List<UUID> getAssets(DataType dataType) {
    List<UUID> uuids =
        switch (dataType) {
          case PRIMARY, PRIMARY_RESULT -> new ArrayList<>(participants);
          case RESULT -> {
            List<UUID> res = new ArrayList<>();
            res.addAll(gridAssets);
            res.addAll(participants);
            res.addAll(ems);
            yield res;
          }
          case EM -> new ArrayList<>(ems);
        };

    if (uuids.isEmpty()) {
      if (dataType == DataType.PRIMARY_RESULT) {
        List<UUID> res = new ArrayList<>();
        res.addAll(extAssets.getOrDefault(DataType.PRIMARY, Collections.emptySet()));
        res.addAll(extAssets.getOrDefault(DataType.RESULT, Collections.emptySet()));

        return res;
      } else {
        return new ArrayList<>(extAssets.getOrDefault(dataType, Collections.emptySet()));
      }

    } else {
      List<UUID> ext = new ArrayList<>();
      extAssets.values().forEach(ext::addAll);

      System.out.println("Ems: "+ems);

      if (extAssets.isEmpty()) {
        return uuids;
      } else {
        return uuids.stream().filter(ext::contains).toList();
      }
    }
  }

  /**
   * Checks if the mapping contains the given id.
   *
   * @param id to check
   * @return {@code true}, if a mapping is found.
   */
  public boolean contains(String id) {
    return idToUuid.containsKey(id);
  }

  /**
   * Checks if the mapping contains the given uuid.
   *
   * @param uuid to check
   * @return {@code true}, if a mapping is found.
   */
  public boolean contains(UUID uuid) {
    return uuidToId.containsKey(uuid);
  }

  /**
   * Method to convert a given id into an uuid.
   *
   * @param id to convert
   * @return the uuid or {@code null}, if no mapping exists
   */
  public UUID from(String id) {
    return idToUuid.get(id);
  }

  /**
   * Method to convert a given uuid into an id.
   *
   * @param uuid to convert
   * @return the uuid or {@code null}, if no mapping exists
   */
  public String from(UUID uuid) {
    return uuidToId.get(uuid);
  }

  /**
   * Method that tries to convert the given id into an uuid.
   *
   * @param id to convert
   * @return an option for an uuid or {@link Optional#empty()}, if no mapping exists
   */
  public Optional<UUID> get(String id) {
    return Optional.ofNullable(idToUuid.get(id));
  }

  /**
   * Method that tries to convert the given uuid into an id.
   *
   * @param uuid to convert
   * @return an option for an id or {@link Optional#empty()}, if no mapping exists
   */
  public Optional<String> get(UUID uuid) {
    return Optional.ofNullable(uuidToId.get(uuid));
  }

  /** Returns the full mapping external id to SIMONA uuid. */
  public Map<String, UUID> getExtId2UuidMapping() {
    return Collections.unmodifiableMap(idToUuid);
  }

  /** Returns the full mapping SIMONA uuid to external id. */
  public Map<UUID, String> getExtUuid2IdMapping() {
    return Collections.unmodifiableMap(uuidToId);
  }
}
