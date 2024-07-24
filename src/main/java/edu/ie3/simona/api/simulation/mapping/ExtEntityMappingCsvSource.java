/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/** Source that is capable of providing information around external mapping from csv files. */
public class ExtEntityMappingCsvSource extends ExtEntityMappingSource {

  private final ExtEntityMapping extEntities;

  private final CsvDataSource dataSource;

  private final Path mappingPath;

  public ExtEntityMappingCsvSource(
      String csvSep, Path folderPath, FileNamingStrategy fileNamingStrategy) {
    super();
    this.dataSource = new CsvDataSource(csvSep, folderPath, fileNamingStrategy);
    this.mappingPath = folderPath;
    this.extEntities = buildExtEntityMapping();
  }

  public ExtEntityMapping getMapping() {
    return extEntities;
  }

  /**
   * Method to retrieve the fields found in the source.
   *
   * @return an option for the found fields
   */
  public Optional<Set<String>> getSourceFields() throws SourceException {
    return dataSource.getSourceFields(ExtEntityEntry.class);
  }

  /**
   * Builds the mapping from CSV
   *
   * @return Mapping external id and SIMONA uuid
   */
  protected final ExtEntityMapping buildExtEntityMapping() {
    return new ExtEntityMapping(buildStream().map(this::createExtEntityEntry).toList());
  }

  private Stream<Map<String, String>>
      buildStream() { // TODO: Interim version -> maybe it would be easier to use PSDM methods for
    // reading the CSV
    Path pathToCsv = mappingPath;

    try (Stream<String> lines = Files.lines(pathToCsv)) {
      // Read the header line
      List<String> headers = lines
              .findFirst()
              .map(line -> Arrays.asList(line.split(",")))
              .orElseThrow(() -> new RuntimeException("No header line found"));

      // Stream the rest of the lines
      Stream<Map<String, String>> mapStream = Files.lines(pathToCsv)
              .skip(1) // Skip the header line
              .map(line -> {
                String[] values = line.split(",");
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                  map.put(headers.get(i), values[i]);
                }
                return map;
              });

      return mapStream;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ExtEntityEntry createExtEntityEntry(Map<String, String> fieldToValues) {
    return new ExtEntityEntry(
        UUID.fromString(fieldToValues.get(ExtEntityFactory.SIMONA_UUID)),
        fieldToValues.get(ExtEntityFactory.EXT_ID),
        ColumnScheme.parse(fieldToValues.get(ExtEntityFactory.COLUMN_SCHEME)).get(),
        fieldToValues.get(ExtEntityFactory.DATA_TYPE));
  }

  public static ExtEntityMapping createExtEntityMapping(Path mappingPath) {
    ExtEntityMappingCsvSource mappingSource =
        new ExtEntityMappingCsvSource(",", mappingPath, new FileNamingStrategy());
    return mappingSource.getMapping();
  }
}
