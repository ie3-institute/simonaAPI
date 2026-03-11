package edu.ie3.simona.api.simulation;

import edu.ie3.simona.api.data.container.ExtInputContainer;
import edu.ie3.simona.api.data.container.ExtOutputContainer;

import java.util.Queue;

public interface ExtCoSimFramework {

    // general methods
    String getName();

    // methods called for initialization

    void setInitDataQueue(Queue<InitData> initDataQueue);

    // methods called during the co-simulation

    Status getStatus(long simonaTick) throws Exception;

    void provideOutputData(ExtOutputContainer outputData);

    void goToNextTick(long simonaTick);

    sealed interface Status permits Finished, HasData, SimonaIsAhead, SimonaIsBehind {}

    record HasData(ExtInputContainer container) implements Status {}

    record SimonaIsBehind(long extTick) implements Status {}

    record SimonaIsAhead() implements Status {}

    record Finished() implements Status {}


    interface InitData {}

    record TickConverter(double scalingFactor) {
        public long toSimonaTick(long extTick) {
           return (long) (extTick * scalingFactor);
        }

        public long toExtTick(long simonaTick) {
            return (long) (simonaTick / scalingFactor);
        }

    }

}
