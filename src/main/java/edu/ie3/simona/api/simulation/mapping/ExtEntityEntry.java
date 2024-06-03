package edu.ie3.simona.api.simulation.mapping;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.input.InputEntity;

import java.util.UUID;

public record ExtEntityEntry(
        UUID uuid,
        String id,
        ColumnScheme columnScheme,
        String resultType
) implements InputEntity {
    public static final String EXT_INPUT = "input";
    public static final String EXT_RESULT_PARTICIPANT = "result_participant";
    public static final String EXT_RESULT_GRID = "result_grid";


    public String toString() {
        return "ExtEntityEntry={"
                + "UUID=" + uuid() + ", "
                + "ExtId=" + id() + ", "
                + "ColumnScheme=" + columnScheme() + ", "
                + "ResultType=" + resultType()
                + "}";
    }
}
