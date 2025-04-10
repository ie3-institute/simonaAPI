package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.result.ResultEntity;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class FlexRequestResult extends ResultEntity {

    private final List<UUID> receivers;

    public FlexRequestResult(ZonedDateTime time, UUID inputModel, List<UUID> receivers) {
        super(time, inputModel);
        this.receivers = receivers;
    }

    public List<UUID> getReceivers() {
        return receivers;
    }

    @Override
    public String toString() {
        return "FlexRequestResult{"
                + "time="
                + getTime()
                + ", inputModel="
                + getInputModel()
                + ", receivers="
                + receivers
                + '}';
    }
}
