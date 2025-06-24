/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Time;
import tech.units.indriya.ComparableQuantity;

public final class FlexOptionRequest {

    public final UUID receiver;
    public final Optional<UUID> sender;
    public final Optional<ComparableQuantity<Time>> delay;

    public FlexOptionRequest(UUID receiver, Optional<UUID> sender) {
        this.receiver = receiver;
        this.sender = sender;
        this.delay = Optional.empty();
    }

    public FlexOptionRequest(UUID receiver, Optional<UUID> sender, Optional<ComparableQuantity<Time>> delay) {
        this.receiver = receiver;
        this.sender = sender;
        this.delay = delay;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FlexOptionRequest that = (FlexOptionRequest) o;
        return Objects.equals(receiver, that.receiver) && Objects.equals(sender, that.sender) && Objects.equals(delay, that.delay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiver, sender, delay);
    }

    @Override
    public String toString() {
        return "FlexOptionRequest{" +
                "receiver=" + receiver +
                ", sender=" + sender +
                ", delay=" + delay +
                '}';
    }
}
