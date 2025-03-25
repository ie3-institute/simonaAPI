package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.result.ResultEntity;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FlexRequestResult extends ResultEntity {

    /**
     * Standard constructor which includes auto generation of the resulting output models uuid.
     *
     * @param time       date and time when the result is produced
     * @param inputModel uuid of the input model that produces the result
     */
    public FlexRequestResult(ZonedDateTime time, UUID inputModel) {
        super(time, inputModel);
    }
}
