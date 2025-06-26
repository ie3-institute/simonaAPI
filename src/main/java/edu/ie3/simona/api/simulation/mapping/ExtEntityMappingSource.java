/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.DataSource;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.simona.api.mapping.ExtEntityMapping;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/** Source for external entity mapping. */
public class ExtEntityMappingSource {

  protected final DataSource dataSource;
  protected final ExtEntityFactory factory;

  protected ExtEntityMappingSource(DataSource dataSource) {
    this.dataSource = dataSource;
    this.factory = new ExtEntityFactory();
  }

  /**
   * Creates an {@link ExtEntityMapping} from a given file.
   *
   * @param filepath path to the file including its name
   * @return a new mapping
   * @throws SourceException if an error occurred
   */
  public static ExtEntityMapping fromFile(Path filepath) throws SourceException {
    String filename = filepath.getFileName().toString();
    Path directoryPath;

    if (!filename.contains(".csv")) {
      directoryPath = filepath;
    } else {
      directoryPath = filepath.getParent();
    }

    ExtEntityNaming naming = new ExtEntityNaming(filename);
    CsvDataSource source = new CsvDataSource(",", directoryPath, new FileNamingStrategy(naming));

    return new ExtEntityMappingSource(source).getMapping();
  }

  /** Return the mapping from a given {@link DataSource}. */
  public ExtEntityMapping getMapping() throws SourceException {
    return new ExtEntityMapping(
        dataSource.getSourceData(ExtEntityEntry.class).map(this::createExtEntityEntry).toList());
  }

  /**
   * Creates an ext entity entry from a given map.
   *
   * @param fieldToValues map: field name to value
   * @return a new {@link ExtEntityEntry}
   */
  private ExtEntityEntry createExtEntityEntry(Map<String, String> fieldToValues) {
    return factory.get(new EntityData(fieldToValues, ExtEntityEntry.class)).getOrThrow();
  }

  /** Csv naming for ext entity mapping. */
  private static class ExtEntityNaming extends EntityPersistenceNamingStrategy {

    private final String filename;

    private ExtEntityNaming(String filename) {
      this.filename = filename.replace(".csv", "");
    }

    @Override
    public Optional<String> getEntityName(Class<? extends Entity> cls) {
      if (ExtEntityEntry.class.isAssignableFrom(cls)) {
        return Optional.of(filename);
      } else {
        return super.getEntityName(cls);
      }
    }
  }
}
