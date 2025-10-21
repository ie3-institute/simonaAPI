package edu.ie3.simona.api.data.model.em


import spock.lang.Shared
import spock.lang.Specification

class FlexOptionRequestTest extends Specification {

    @Shared
    private UUID receiverUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    @Shared
    private UUID senderUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    def "The FlexOptionRequest can be constructed with sender correctly"() {
        when:
        def request = new FlexOptionRequest(receiverUuid, false)

        then:
        request.receiver == receiverUuid
        !request.disaggregated
    }

}
