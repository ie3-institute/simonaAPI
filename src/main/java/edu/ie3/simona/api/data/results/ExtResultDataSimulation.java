/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.simona.api.data.ExtDataSimulation;

/**
 * An external simulation that needs results from SIMONA should implement this interface and handle
 * the ExtResultsData that is handed over.
 */
public interface ExtResultDataSimulation extends ExtDataSimulation {

  /** Hand over an ExtPrimaryData which enables communication regarding primary data. */
  void setExtResultData(ExtResultData extResultData);

  /** Should implement the convertion of the PSDM format to the external format of result data. */
  ResultDataFactory getResultDataFactory();
}
