/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.simona.api.exceptions.ConvertionException;

public interface ResultDataFactory {

  /**
   * Should convert a result entity to an object, that can be read by the external simulation, with
   * a check if the object is primary data
   */
  Object convert(ResultEntity entity) throws ConvertionException;
}
