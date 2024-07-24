/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtEntityFactory extends EntityFactory<ExtEntityEntry, EntityData> {

  public static final String SIMONA_UUID = "uuid";
  public static final String EXT_ID = "id";
  public static final String COLUMN_SCHEME = "columnScheme";
  public static final String DATA_TYPE = "dataType";

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return Collections.singletonList(
        Stream.of(SIMONA_UUID, EXT_ID, COLUMN_SCHEME, DATA_TYPE).collect(Collectors.toSet()));
  }

  @Override
  protected ExtEntityEntry buildModel(EntityData data) {
    UUID simonaUuid = data.getUUID(SIMONA_UUID);
    String extId = data.getField(EXT_ID);
    Optional<ColumnScheme> columnScheme = ColumnScheme.parse(COLUMN_SCHEME);
    String inputType = data.getField(DATA_TYPE);

    return new ExtEntityEntry(
        simonaUuid,
        extId,
        columnScheme
            .orElseThrow(), // FIXME: Interim version -> ColumnScheme should handle more data types
        inputType);
  }
}
