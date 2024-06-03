/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.primarydata;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.simona.api.data.ExtDataSimulation;
import edu.ie3.simona.api.data.ExtInputDataPackage;
import edu.ie3.simona.api.exceptions.ConvertionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An external simulation that provides primary data should implement this interface and handle the
 * ExtPrimaryData that is handed over.
 */
public class ExtPrimaryDataSimulation implements ExtDataSimulation {

  private final List<UUID> primaryDataAssets;

  private final Map<String, UUID> extPrimaryDataMapping;

  private final PrimaryDataFactory primaryDataFactory;

  private ExtPrimaryData extPrimaryData;

  public ExtPrimaryDataSimulation(
          PrimaryDataFactory primaryDataFactory,
          Map<String, UUID> extPrimaryDataMapping
  ) {
    this.primaryDataFactory = primaryDataFactory;
    this.extPrimaryDataMapping = extPrimaryDataMapping;
    this.primaryDataAssets = this.extPrimaryDataMapping.values().stream().toList();
  }

  /** Hand over an ExtPrimaryData which enables communication regarding primary data. */
  public void setExtPrimaryData(ExtPrimaryData extPrimaryData) {
    this.extPrimaryData = extPrimaryData;
  }

  public PrimaryDataFactory getPrimaryDataFactory() {
    return primaryDataFactory;
  }

  public List<UUID> getPrimaryDataAssets() {
    return primaryDataAssets;
  }

  public ExtPrimaryData getExtPrimaryData() {
    return extPrimaryData;
  }


  public Map<UUID, Value> createExtPrimaryDataMap(
          ExtInputDataPackage extInputDataPackage
  ) {
    Map<UUID, Value> primaryDataForSimona = new HashMap<>();
    extInputDataPackage.getSimonaInputMap().forEach(
            (id, extInput) -> {
              if (extPrimaryDataMapping.containsKey(id)) {
                try {
                  primaryDataForSimona.put(
                          extPrimaryDataMapping.get(id),
                          primaryDataFactory.convert(extInput)
                  );
                } catch (ConvertionException e) {
                  throw new RuntimeException(e);
                }
              }
            }
    );
    return primaryDataForSimona;
  }
}
