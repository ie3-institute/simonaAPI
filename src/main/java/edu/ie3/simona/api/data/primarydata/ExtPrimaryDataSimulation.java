/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.primarydata;

import edu.ie3.simona.api.data.ExtDataSimulation;

/**
 * An external simulation that provides primary data should implement this interface and handle the
 * ExtPrimaryData that is handed over.
 */
public interface ExtPrimaryDataSimulation extends ExtDataSimulation {

  /**
   * Hand over an ExtPrimaryData which enables communication regarding primary data.
   */
  void setExtPrimaryData(ExtPrimaryData extPrimaryData);

  /**
   * Should implement the convertion of the external format to the PSDM format of primary data.
   */
  PrimaryDataFactory getPrimaryDataFactory();
}
