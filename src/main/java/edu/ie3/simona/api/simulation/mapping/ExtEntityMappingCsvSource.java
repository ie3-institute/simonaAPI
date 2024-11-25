/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import java.nio.file.Path;
import java.util.Map;

/** Source that is capable of providing information around external mapping from csv files. */
public class ExtEntityMappingCsvSource extends ExtEntityMappingSource {

  private final ExtEntityMapping extEntities;

  private final CsvDataSource dataSource;

  public ExtEntityMappingCsvSource(
      String csvSep, Path folderPath, FileNamingStrategy fileNamingStrategy)
      throws SourceException {
    super();
    this.dataSource = new CsvDataSource(csvSep, folderPath, fileNamingStrategy);

    this.extEntities = buildExtEntityMapping();
  }

  public ExtEntityMapping getMapping() {
    return extEntities;
  }

  /**
   * Builds the mapping from CSV
   *
   * @return Mapping external id and SIMONA uuid
   */
  protected final ExtEntityMapping buildExtEntityMapping() throws SourceException {
    return new ExtEntityMapping(
        dataSource.getSourceData(ExtEntityEntry.class).map(this::createExtEntityEntry).toList());
  }

  private ExtEntityEntry createExtEntityEntry(Map<String, String> fieldToValues) {
    return factory.get(new EntityData(fieldToValues, ExtEntityEntry.class)).getOrThrow();
  }

  public static ExtEntityMapping createExtEntityMapping(Path mappingPath) throws SourceException {
    Path directory = mappingPath.getParent();
    ExtEntityMappingCsvSource mappingSource =
        new ExtEntityMappingCsvSource(",", directory, new FileNamingStrategy(namingStrategy));
    return mappingSource.getMapping();
  }
}
