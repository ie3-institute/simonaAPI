package edu.ie3.simona.api.simulation

import static edu.ie3.simona.api.simulation.ExtCoSimFramework.*

import spock.lang.Specification

class ExtCoSimFrameworkTest extends Specification {

    def "The TickConverter should convert a tick to the SIMONA timescale correctly"() {
        when:
        def convertedTick = converter.toSimonaTick(extTick)

        then:
        convertedTick == simonaTick

        where:
        converter                | extTick | simonaTick
        new TickConverter(1d)    | 1L      | 1L
        new TickConverter(1d)    | 100L    | 100L
        new TickConverter(0.001) | 1L      | 0L
        new TickConverter(0.001) | 100L    | 0L
        new TickConverter(0.001) | 1000L   | 1L
        new TickConverter(0.001) | 2000L   | 2L
        new TickConverter(1000d) | 1L      | 1000L
        new TickConverter(1000d) | 100L    | 100000L
        new TickConverter(1000d) | 1000L   | 1000000L
        new TickConverter(1000d) | 2000L   | 2000000L
    }

    def "The TickConverter should convert a SIMONA tick to the external timescale correctly"() {
        when:
        def convertedTick = converter.toExtTick(simonaTick)

        then:
        convertedTick == extTick

        where:
        converter                | simonaTick | extTick
        new TickConverter(1d)    | 1L      | 1L
        new TickConverter(1d)    | 100L    | 100L
        new TickConverter(0.001) | 1L      | 1000L
        new TickConverter(0.001) | 100L    | 100000L
        new TickConverter(0.001) | 1000L   | 1000000L
        new TickConverter(0.001) | 2000L   | 2000000L
        new TickConverter(1000d) | 1L      | 0L
        new TickConverter(1000d) | 100L    | 0L
        new TickConverter(1000d) | 1000L   | 1L
        new TickConverter(1000d) | 2000L   | 2L
    }
}
