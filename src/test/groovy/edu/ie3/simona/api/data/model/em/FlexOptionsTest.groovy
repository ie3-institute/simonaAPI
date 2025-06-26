package edu.ie3.simona.api.data.model.em

import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Power

class FlexOptionsTest extends Specification {

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

    def "FlexOptions can be constructed without delay correctly"() {
        when:
        def flexOptions = new FlexOptions(receiverUuid, senderUuid, pRef, pMin, pMax)

        then:
        flexOptions.receiver == receiverUuid
        flexOptions.sender == senderUuid
        flexOptions.pRef == pRef
        flexOptions.pMin == pMin
        flexOptions.pMax == pMax
        flexOptions.delay == Optional.empty()
        !flexOptions.hasDelay()
    }

    def "FlexOptions can be constructed with delay correctly"() {
        given:
        def delay = Quantities.getQuantity(10, PowerSystemUnits.MILLISECOND)

        when:
        def flexOptions = new FlexOptions(receiverUuid, senderUuid, pRef, pMin, pMax, Optional.of(delay))

        then:
        flexOptions.receiver == receiverUuid
        flexOptions.sender == senderUuid
        flexOptions.pRef == pRef
        flexOptions.pMin == pMin
        flexOptions.pMax == pMax
        flexOptions.delay == Optional.of(delay)
        flexOptions.hasDelay()
    }

}
