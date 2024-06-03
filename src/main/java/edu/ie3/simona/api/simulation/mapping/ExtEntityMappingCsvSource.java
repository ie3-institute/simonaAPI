package edu.ie3.simona.api.simulation.mapping;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExtEntityMappingCsvSource extends ExtEntityMappingSource {
    private final ExtEntityMapping extEntities;
    private final CsvDataSource dataSource;

    private final Path mappingPath;

    public ExtEntityMappingCsvSource(String csvSep, Path folderPath, FileNamingStrategy fileNamingStrategy) {
        super();
        this.dataSource = new CsvDataSource(csvSep, folderPath, fileNamingStrategy);
        this.mappingPath = folderPath;
        this.extEntities = buildExtEntityMapping();
    }

    public ExtEntityMapping getMapping() {
        return extEntities;
    }


    public Optional<Set<String>> getSourceFields() throws SourceException {
        return dataSource.getSourceFields(ExtEntityEntry.class);
    }

    protected ExtEntityMapping buildExtEntityMapping() {
        return new ExtEntityMapping(buildStream().map(this::createExtEntityEntry).toList());
    }

    private Stream<Map<String, String>> buildStream() {
        try {
            FileReader fileReader = new FileReader(String.valueOf(mappingPath));
            CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(fileReader);

            // Creating a Stream from CSVReader
            Stream<Map<String, String>> csvStream = StreamSupport.stream(
                    new Spliterators.AbstractSpliterator<Map<String, String>>(Long.MAX_VALUE, Spliterator.ORDERED) {
                        @Override
                        public boolean tryAdvance(Consumer<? super Map<String, String>> action) {
                            try {
                                Map<String, String> line = csvReader.readMap();
                                if (line == null) {
                                    return false;
                                }
                                action.accept(line);
                                //System.out.println(line);
                                return true;
                            } catch (IOException | CsvValidationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, false);

            //csvReader.close();
            return csvStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ExtEntityEntry createExtEntityEntry(
            Map<String, String> fieldToValues
    ) {
        return new ExtEntityEntry(
                UUID.fromString(fieldToValues.get(ExtEntityFactory.SIMONA_UUID)),
                fieldToValues.get(ExtEntityFactory.EXT_ID),
                ColumnScheme.parse(fieldToValues.get(ExtEntityFactory.COLUMN_SCHEME)).get(),
                fieldToValues.get(ExtEntityFactory.DATA_TYPE)
        );
    }

    public static ExtEntityMapping createExtEntityMapping(Path mappingPath) {
        ExtEntityMappingCsvSource mappingSource = new ExtEntityMappingCsvSource(
                ",",
                mappingPath,
                new FileNamingStrategy()
        );
        return mappingSource.getMapping();
    }

    /*
    private Try<ExtEntityEntry, FactoryException> createExtEntityEntry(
            Map<String, String> fieldToValues
    ) {
        System.out.println("create: " + fieldToValues);
        EntityData entityData = new EntityData(fieldToValues, ExtEntityEntry.class);
        return factory.get(entityData);
    }
    */
}
