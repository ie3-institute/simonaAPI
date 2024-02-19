package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.ResultEntity;

public interface ResultDataFactory {
    Object convertResultToString(ResultEntity entity) throws Exception;
}
