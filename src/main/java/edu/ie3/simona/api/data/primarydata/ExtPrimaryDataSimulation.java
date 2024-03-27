/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.primarydata;

import edu.ie3.simona.api.data.ExtDataSimulation;

import java.util.List;
import java.util.UUID;

/**
 * An external simulation that provides primary data should implement this interface and handle the
 * ExtPrimaryData that is handed over.
 */
public class ExtPrimaryDataSimulation implements ExtDataSimulation {

  private final List<UUID> primaryDataAssets;

  private final PrimaryDataFactory primaryDataFactory;

  private ExtPrimaryData extPrimaryData;

  public ExtPrimaryDataSimulation(
          PrimaryDataFactory primaryDataFactory,
          List<UUID> primaryDataAssets
  ) {
    this.primaryDataFactory = primaryDataFactory;
    this.primaryDataAssets = primaryDataAssets;
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
}
