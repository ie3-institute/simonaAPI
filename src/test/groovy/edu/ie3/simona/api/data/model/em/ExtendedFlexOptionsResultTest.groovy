package edu.ie3.simona.api.data.model.em

import edu.ie3.datamodel.models.result.system.FlexOptionsResult
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Power
import java.time.ZonedDateTime

class ExtendedFlexOptionsResultTest extends Specification {

    @Shared
    ZonedDateTime time = ZonedDateTime.now()

    @Shared
    UUID receiverUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    @Shared
    UUID senderUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    @Shared
    ComparableQuantity<Power> pRef = Quantities.getQuantity(7, PowerSystemUnits.KILOWATT)

    @Shared
    ComparableQuantity<Power> pMin = Quantities.getQuantity(0, PowerSystemUnits.KILOWATT)

    @Shared
    ComparableQuantity<Power> pMax = Quantities.getQuantity(10, PowerSystemUnits.KILOWATT)


    def "The ExtendedFlexOptionsResult can be constructed without disaggregated flex options"() {
        when:
        def result = new ExtendedFlexOptionsResult(time, senderUuid, receiverUuid, pRef, pMin, pMax, [:])

        then:
        result.time == time
        result.inputModel == senderUuid
        result.sender == senderUuid
        result.receiver == receiverUuid
        result.pRef == pRef
        result.pMin == pMin
        result.pMax == pMax
        result.disaggregated == [:]
    }

    def "The ExtendedFlexOptionsResult can be constructed with disaggregated flex options"() {
        given:
        def dis1 = UUID.fromString("a246eee3-405c-4af1-9ad2-69ecad2bfb65")
        def dis2 = UUID.fromString("78676121-f154-4f70-ad50-4384ddf8deed")

        def disaggregated = [
                (dis1): new FlexOptionsResult(time, dis1, pRef, pMin, pMax),
                (dis2): new FlexOptionsResult(time, dis2, pMin, pMin, pMin)
        ]

        when:
        def result = new ExtendedFlexOptionsResult(time, senderUuid, receiverUuid, pRef, pMin, pMax, disaggregated)

        then:
        result.time == time
        result.inputModel == senderUuid
        result.sender == senderUuid
        result.receiver == receiverUuid
        result.pRef == pRef
        result.pMin == pMin
        result.pMax == pMax
        result.disaggregated == disaggregated
    }

    def "The ExtendedFlexOptionsResult should specify if there are disaggregated flex options correctly"() {
        when:
        def result = new ExtendedFlexOptionsResult(time, senderUuid, receiverUuid, pRef, pMin, pMax, diagregatedMap)

        then:
        result.hasDisaggregated() == expectedResult

        where:
        diagregatedMap | expectedResult
        [:] as Map     | false
        [
            (UUID.fromString("a246eee3-405c-4af1-9ad2-69ecad2bfb65")): new FlexOptionsResult(time, UUID.fromString("a246eee3-405c-4af1-9ad2-69ecad2bfb65"), pRef, pMin, pMax),
            (UUID.fromString("78676121-f154-4f70-ad50-4384ddf8deed")): new FlexOptionsResult(time, UUID.fromString("78676121-f154-4f70-ad50-4384ddf8deed"), pMin, pMin, pMin)
        ]              | true
    }

}
