/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Optional;
import java.util.UUID;

/**
 * Container for an external asset with all information for the mapping
 *
 * @param uuid SIMONA uuid
 * @param id external id
 * @param columnScheme option for data types the external asset expects
 * @param dataType data types the external asset expects
 */
public record ExtEntityEntry(
    UUID uuid, String id, Optional<ColumnScheme> columnScheme, DataType dataType)
    implements InputEntity {

  public String toString() {
    return "ExtEntityEntry={"
        + "UUID="
        + uuid
        + ", "
        + "extId="
        + id
        + ", "
        + "columnScheme="
        + columnScheme
        + ", "
        + "dataType="
        + dataType
        + "}";
  }
}
