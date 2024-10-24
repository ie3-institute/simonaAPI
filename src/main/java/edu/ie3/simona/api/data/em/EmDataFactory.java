/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.ExtInputDataValue;
import edu.ie3.simona.api.exceptions.ConvertionException;

/** Interface that should be implemented by an external simulation. */
public interface EmDataFactory {

  /**
   * Should convert an object to an em data value with a check if the object has a valid data type
   */
  PValue convert(ExtInputDataValue entity) throws ConvertionException;
}