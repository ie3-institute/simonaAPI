package edu.ie3.simona.api.data.model.em

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Power
import java.time.ZonedDateTime

class EmSetPointResultTest extends Specification {

    @Shared
    ZonedDateTime time = ZonedDateTime.now()

    @Shared
    UUID senderUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    @Shared
    ComparableQuantity<Power> power = Quantities.getQuantity(10, PowerSystemUnits.KILOWATT)


    def "An EmSetPoint can be constructed without set points correctly"() {
        when:
        def setPoint = new EmSetPointResult(time, senderUuid, [:])

        then:
        setPoint.time == time
        setPoint.inputModel == senderUuid
        setPoint.sender == senderUuid
        setPoint.receiverToSetPoint == [:]
    }

    def "An EmSetPoint can be constructed with set points correctly"() {
        given:
        def receiverUuid = UUID.fromString("81c7c8de-0f01-4559-97bb-844259e467b5")

        def setPoints = [(receiverUuid): new PValue(power)]

        when:
        def setPoint = new EmSetPointResult(time, senderUuid, setPoints)

        then:
        setPoint.time == time
        setPoint.inputModel == senderUuid
        setPoint.sender == senderUuid
        setPoint.receiverToSetPoint == setPoints
    }

}
