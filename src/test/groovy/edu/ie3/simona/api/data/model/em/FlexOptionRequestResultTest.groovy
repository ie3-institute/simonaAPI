package edu.ie3.simona.api.data.model.em

import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime

class FlexOptionRequestResultTest extends Specification {

    @Shared
    ZonedDateTime time = ZonedDateTime.now()

    @Shared
    UUID receiverUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    @Shared
    UUID senderUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    def "An FlexOptionRequestResult can be constructed correctly"() {
        when:
        def result = new FlexOptionRequestResult(time, senderUuid, [receiverUuid])

        then:
        result.time == time
        result.inputModel == senderUuid
        result.sender == senderUuid
        result.receivers == [receiverUuid]
    }

}
