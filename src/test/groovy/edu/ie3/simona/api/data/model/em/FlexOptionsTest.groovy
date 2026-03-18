package edu.ie3.simona.api.data.model.em

import edu.ie3.util.interval.ClosedInterval
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
    ComparableQuantity<Power> pRef = Quantities.getQuantity(7d, KILOWATT)

    @Shared
    ComparableQuantity<Power> pMin = Quantities.getQuantity(0d, KILOWATT)

    @Shared
    ComparableQuantity<Power> pMax = Quantities.getQuantity(10d, KILOWATT)

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
    }

    def "GeneralFlexOptions can be constructed correctly"() {
        given:
        UUID receiver = UUID.randomUUID()

        when:
        def assetEnergyBoundary = new EnergyBoundariesFlexOptions.AssetEnergyBoundaries(
                [:],
                new ClosedInterval(Quantities.getQuantity(0d, KILOWATT), Quantities.getQuantity(10d, KILOWATT)),
                Quantities.getQuantity(95d, PERCENT),
                Quantities.getQuantity(95d, PERCENT),
                OptionalLong.empty()
        )

        def flexOptions = new EnergyBoundariesFlexOptions(
                receiver,
                senderUuid,
                [assetEnergyBoundary]
        )

        then:
        assetEnergyBoundary.energyLimits == [:]
        assetEnergyBoundary.powerLimits.lower == Quantities.getQuantity(0d, KILOWATT)
        assetEnergyBoundary.powerLimits.upper == Quantities.getQuantity(10d, KILOWATT)
        assetEnergyBoundary.etaCharge == Quantities.getQuantity(95d, PERCENT)
        assetEnergyBoundary.etaDischarge == Quantities.getQuantity(95d, PERCENT)
        assetEnergyBoundary.tickDisconnect == OptionalLong.empty()

        flexOptions.receiver == receiver
        flexOptions.model == senderUuid
        flexOptions.energyBoundaries == [assetEnergyBoundary]
    }

}
