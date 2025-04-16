package edu.ie3.simona.api.data.em.model;

import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Power;
import java.time.ZonedDateTime;
import java.util.UUID;

public class ExtendedFlexOptionsResult extends FlexOptionsResult {

    private final UUID receiver;

    /**
     * Standard constructor with automatic uuid generation.
     *
     * @param time       date and time when the result is produced
     * @param sender uuid of the input model that produces the result
     * @param receiver uuid of the receiver that will receive this result
     * @param pRef       active power that was suggested for regular usage by the system participant
     * @param pMin       active minimal power that was determined by the system participant
     * @param pMax       active maximum power that was determined by the system participant
     */
    public ExtendedFlexOptionsResult(ZonedDateTime time, UUID sender, UUID receiver, ComparableQuantity<Power> pRef, ComparableQuantity<Power> pMin, ComparableQuantity<Power> pMax) {
        super(time, sender, pRef, pMin, pMax);
        this.receiver = receiver;
    }

    public UUID getSender() {
        return getInputModel();
    }

    public UUID getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return "ExtendedFlexOptionsResult{"
                                + "time="
                                + getTime()
                                + ", sender="
                                + getSender()
                + ", receiver="
                + receiver
                + ", pRef="
                + getpRef()
                + ", pMin="
                + getpMin()
                + ", pMax="
                + getpMax()
                + '}';
    }
}
