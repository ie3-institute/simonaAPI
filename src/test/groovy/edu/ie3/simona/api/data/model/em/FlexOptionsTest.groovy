package edu.ie3.simona.api.data.model.em


import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Power

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static tech.units.indriya.unit.Units.PERCENT

class FlexOptionsTest extends Specification {

    @Shared
    UUID senderUuid = UUID.fromString("978554e5-32cc-4221-bd39-84beac60f327")

    @Shared
    ComparableQuantity<Power> pRef = Quantities.getQuantity(7, KILOWATT)

    @Shared
    ComparableQuantity<Power> pMin = Quantities.getQuantity(0, KILOWATT)

    @Shared
    ComparableQuantity<Power> pMax = Quantities.getQuantity(10, KILOWATT)

    def "PowerLimitFlexOptions can be constructed correctly"() {
        given:
        UUID receiver = UUID.randomUUID()

        when:
        def flexOptions = new PowerLimitFlexOptions(receiver, senderUuid, pRef, pMin, pMax)

        then:
        flexOptions.receiver == receiver
        flexOptions.model == senderUuid
        flexOptions.pRef == pRef
        flexOptions.pMin == pMin
        flexOptions.pMax == pMax
        flexOptions.disaggregated == [:]
    }

    def "GeneralFlexOptions can be constructed correctly"() {
        given:
        UUID receiver = UUID.randomUUID()

        when:
        def flexOptions = new EnergyBoundariesFlexOptions(
                receiver,
                senderUuid,
                "general flex type",
                Quantities.getQuantity(0, KILOWATT),
                Quantities.getQuantity(10, KILOWATT),
                Quantities.getQuantity(95, PERCENT),
                Quantities.getQuantity(95, PERCENT),
                [:],
        )

        then:
        flexOptions.receiver == receiver
        flexOptions.model == senderUuid
        flexOptions.flexType == "general flex type"
        flexOptions.pMin == Quantities.getQuantity(0, KILOWATT)
        flexOptions.pMax == Quantities.getQuantity(10, KILOWATT)
        flexOptions.etaCharge == Quantities.getQuantity(95, PERCENT)
        flexOptions.etaDischarge == Quantities.getQuantity(95, PERCENT)
        flexOptions.tickToEnergyLimits == [:]
        flexOptions.disaggregated == [:]
    }

}
