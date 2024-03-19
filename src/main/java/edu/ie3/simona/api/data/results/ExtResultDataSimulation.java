/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.simona.api.data.ExtDataSimulation;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryData;
import edu.ie3.simona.api.data.primarydata.PrimaryDataFactory;

import java.util.List;
import java.util.UUID;

/**
 * An external simulation that needs results from SIMONA should implement this interface and handle
 * the ExtResultsData that is handed over.
 */
public class ExtResultDataSimulation implements ExtDataSimulation {
  private final List<UUID> resultDataAssets;

  private final ResultDataFactory resultDataFactory;

  private ExtResultData extResultData;

  public ExtResultDataSimulation(
          ResultDataFactory resultDataFactory,
          List<UUID> resultDataAssets
  ) {
    this.resultDataFactory = resultDataFactory;
    this.resultDataAssets = resultDataAssets;
  }

  /** Hand over an ExtPrimaryData which enables communication regarding primary data. */
  public void setExtResultData(ExtResultData extResultData) {
    this.extResultData = extResultData;
  }

  /** Should implement the convertion of the external format to the PSDM format of primary data. */
  public ResultDataFactory getResultDataFactory() {
    return resultDataFactory;
  }

  /** Should implement the list of assets that provide primary data to SIMOAN */
  public List<UUID> getResultDataAssets() {
    return resultDataAssets;
  }

  public ExtResultData getExtResultData() {
    return extResultData;
  }
}
