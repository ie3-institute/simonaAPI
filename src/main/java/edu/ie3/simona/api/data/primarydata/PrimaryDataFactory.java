/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.primarydata;

import edu.ie3.datamodel.models.value.Value;

/** Interface that should be implemented by an external simulation. */
public interface PrimaryDataFactory {

  /**
   * Should convert an object to an primary data value with a check if the object is primary data
   */
  Value convertObjectToValue(Object entity) throws Exception;
}
