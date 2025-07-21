package edu.ie3.simona.api.data.model.em

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Power

class EmSetPointTest extends Specification {

    @Shared
    UUID receiverUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    @Shared
    ComparableQuantity<Power> power = Quantities.getQuantity(10, PowerSystemUnits.KILOWATT)


    def "An empty EmSetPoint can be constructed correctly"() {
        when:
        def setPoint = new EmSetPoint(receiverUuid)

        then:
        setPoint.receiver == receiverUuid
        setPoint.power == Optional.empty()
    }

    def "An EmSetPoint can be constructed correctly"() {
        given:
        def pValue = new PValue(power)

        when:
        def setPoint1 = new EmSetPoint(receiverUuid, power)
        def setPoint2 = new EmSetPoint(receiverUuid, pValue)

        then:
        setPoint1.receiver == receiverUuid
        setPoint1.power == Optional.of(pValue)

        setPoint2.receiver == receiverUuid
        setPoint2.power == Optional.of(pValue)
    }

}
