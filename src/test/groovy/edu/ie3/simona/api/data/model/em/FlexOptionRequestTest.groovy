package edu.ie3.simona.api.data.model.em

import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class FlexOptionRequestTest extends Specification {

    @Shared
    UUID receiverUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    @Shared
    UUID senderUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    def "The FlexOptionRequest can be constructed without sender and delay correctly"() {
        when:
        def request = new FlexOptionRequest(receiverUuid)

        then:
        request.receiver == receiverUuid
        request.sender == Optional.empty()
        request.delay == Optional.empty()
        !request.hasDelay()
    }

    def "The FlexOptionRequest can be constructed with sender and without delay correctly"() {
        when:
        def request = new FlexOptionRequest(receiverUuid, senderUuid)

        then:
        request.receiver == receiverUuid
        request.sender == Optional.of(senderUuid)
        request.delay == Optional.empty()
        !request.hasDelay()
    }

    def "The FlexOptionRequest can be constructed with sender as optional and without delay correctly"() {
        when:
        def request = new FlexOptionRequest(receiverUuid, Optional.of(senderUuid))

        then:
        request.receiver == receiverUuid
        request.sender == Optional.of(senderUuid)
        request.delay == Optional.empty()
        !request.hasDelay()
    }

    def "The FlexOptionRequest can be constructed with sender and delay correctly"() {
        given:
        def delay = Quantities.getQuantity(10, PowerSystemUnits.MILLISECOND)

        when:
        def request = new FlexOptionRequest(receiverUuid, Optional.of(senderUuid), Optional.of(delay))

        then:
        request.receiver == receiverUuid
        request.sender == Optional.of(senderUuid)
        request.delay == Optional.of(delay)
        request.hasDelay()
    }

}
