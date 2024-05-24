/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.simona.api.data.ExtDataSimulation;

import java.util.List;
import java.util.UUID;

/**
 * An external simulation that needs results from SIMONA should implement this interface and handle
 * the ExtResultsData that is handed over.
 */
public class ExtResultDataSimulation implements ExtDataSimulation {

  private final List<UUID> particpantResultDataAssets;
  private final List<UUID> gridResultDataAssets;

  private final ResultDataFactory resultDataFactory;

  private ExtResultData extResultData;

  public ExtResultDataSimulation(
          ResultDataFactory resultDataFactory,
          List<UUID> particpantResultDataAssets,
          List<UUID> gridResultDataAssets
  ) {
    this.resultDataFactory = resultDataFactory;
    this.particpantResultDataAssets = particpantResultDataAssets;
    this.gridResultDataAssets = gridResultDataAssets;
  }

  /** Hand over an ExtPrimaryData which enables communication regarding primary data. */
  public void setExtResultData(ExtResultData extResultData) {
    this.extResultData = extResultData;
  }

  public ResultDataFactory getResultDataFactory() {
    return resultDataFactory;
  }

  public List<UUID> getParticipantResultDataAssets() {
    return particpantResultDataAssets;
  }
  public List<UUID> getGridResultDataAssets() {
    return gridResultDataAssets;
  }

  public ExtResultData getExtResultData() {
    return extResultData;
  }
}
