package edu.ie3.simona.api.data

import spock.lang.Shared
import spock.lang.Specification

class SetupDataSpec extends Specification {

    @Shared
    private SetupData setupData = new SetupData(["abc", "123"] as String[], null, null, null)

    def "SetupData.equals() should work correctly"() {
        when:
        def result = Objects.equals(setupData, data)

        then:
        result == expectedResults

        where:
        data | expectedResults
        new SetupData(["abc", "123"] as String[], null, null, null) | true
        new SetupData(["abcd", "123"] as String[], null, null, null) | false
    }

    def "SetupData.toString() should work correctly"() {
        when:
        def str = setupData.toString()

        then:
        str == "SetupData{mainArgs=[abc, 123], config=null, gridContainer=null, baseOutputDirectory=null}"
    }

}
