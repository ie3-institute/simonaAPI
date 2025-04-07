package edu.ie3.simona.api.data.container

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simona.api.data.model.em.FlexOptionRequest
import edu.ie3.simona.api.data.model.em.FlexOptions
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT

class ExtInputContainerTest extends Specification {

    def "An ExtInputContainer should add primary data correctly"() {
        given:
        UUID uuid = UUID.randomUUID()
        def value = new PValue(Quantities.getQuantity(10d, KILOWATT))

        def container = new ExtInputContainer(0L)

        when:
        container.addPrimaryValue(uuid, value)

        then:
        container.primaryData == [(uuid): value]
    }

    def "An ExtInputContainer should add flex option request data correctly"() {
        given:
        UUID requester = UUID.randomUUID()
        UUID uuid = UUID.randomUUID()

        def container = new ExtInputContainer(0L)

        when:
        container.addRequest(requester, [uuid])

        then:
        container.flexRequests == [(requester): new FlexOptionRequest(requester, [uuid])]
    }

    def "An ExtInputContainer should add flex option data correctly"() {
        given:
        UUID receiver = UUID.randomUUID()
        UUID sender = UUID.randomUUID()
        def flexOptions = new FlexOptions(sender, Quantities.getQuantity(0d, KILOWATT), Quantities.getQuantity(2d, KILOWATT), Quantities.getQuantity(5d, KILOWATT))

        def container = new ExtInputContainer(0L)

        when:
        container.addFlexOptions(receiver, [flexOptions])

        then:
        container.flexOptions == [(receiver): [flexOptions]]
    }

    def "An ExtInputContainer should add set point data correctly"() {
        given:
        UUID uuid = UUID.randomUUID()
        def setPoint = new PValue(Quantities.getQuantity(5d, KILOWATT))

        def container = new ExtInputContainer(0L)

        when:
        container.addSetPoint(uuid, setPoint)

        then:
        container.setPoints == [(uuid): setPoint]
    }

    def "An ExtInputContainer should extract primary data correctly"() {
        given:
        def container = new ExtInputContainer(0L)

        UUID primaryUuid = UUID.randomUUID()
        PValue primaryValue = new PValue(Quantities.getQuantity(10d, KILOWATT))
        container.addPrimaryValue(primaryUuid, primaryValue)

        UUID requester = UUID.randomUUID()
        UUID requested = UUID.randomUUID()
        container.addRequest(requester, [requested])

        UUID receiver = UUID.randomUUID()
        def flexOptions = new FlexOptions(UUID.randomUUID(), Quantities.getQuantity(0d, KILOWATT), Quantities.getQuantity(2d, KILOWATT), Quantities.getQuantity(5d, KILOWATT))
        container.addFlexOptions(receiver, [flexOptions])

        UUID emAsset = UUID.randomUUID()
        def setPoint = new PValue(Quantities.getQuantity(5d, KILOWATT))
        container.addSetPoint(emAsset, setPoint)

        when:
        def extracted = container.extractPrimaryData()

        then:
        extracted.size() == 1
        extracted == [(primaryUuid): primaryValue]

        container.primaryData.size() == 0
        container.flexRequests.size() == 1
        container.flexOptions.size() == 1
        container.setPoints.size() == 1
    }

    def "An ExtInputContainer should extract flex option request data correctly"() {
        given:
        def container = new ExtInputContainer(0L)

        UUID primaryUuid = UUID.randomUUID()
        PValue primaryValue = new PValue(Quantities.getQuantity(10d, KILOWATT))
        container.addPrimaryValue(primaryUuid, primaryValue)

        UUID requester = UUID.randomUUID()
        UUID requested = UUID.randomUUID()
        container.addRequest(requester, [requested])

        UUID receiver = UUID.randomUUID()
        def flexOptions = new FlexOptions(UUID.randomUUID(), Quantities.getQuantity(0d, KILOWATT), Quantities.getQuantity(2d, KILOWATT), Quantities.getQuantity(5d, KILOWATT))
        container.addFlexOptions(receiver, [flexOptions])

        UUID emAsset = UUID.randomUUID()
        def setPoint = new PValue(Quantities.getQuantity(5d, KILOWATT))
        container.addSetPoint(emAsset, setPoint)

        when:
        def extracted = container.extractFlexRequests()

        then:
        extracted.size() == 1
        extracted == [(requester): new FlexOptionRequest(requester, [requested])]

        container.primaryData.size() == 1
        container.flexRequests.size() == 0
        container.flexOptions.size() == 1
        container.setPoints.size() == 1
    }

    def "An ExtInputContainer should extract flex option data correctly"() {
        given:
        def container = new ExtInputContainer(0L)

        UUID primaryUuid = UUID.randomUUID()
        PValue primaryValue = new PValue(Quantities.getQuantity(10d, KILOWATT))
        container.addPrimaryValue(primaryUuid, primaryValue)

        UUID requester = UUID.randomUUID()
        UUID requested = UUID.randomUUID()
        container.addRequest(requester, [requested])

        UUID receiver = UUID.randomUUID()
        def flexOptions = new FlexOptions(UUID.randomUUID(), Quantities.getQuantity(0d, KILOWATT), Quantities.getQuantity(2d, KILOWATT), Quantities.getQuantity(5d, KILOWATT))
        container.addFlexOptions(receiver, [flexOptions])

        UUID emAsset = UUID.randomUUID()
        def setPoint = new PValue(Quantities.getQuantity(5d, KILOWATT))
        container.addSetPoint(emAsset, setPoint)

        when:
        def extracted = container.extractFlexOptions()

        then:
        extracted.size() == 1
        extracted == [(receiver): [flexOptions]]

        container.primaryData.size() == 1
        container.flexRequests.size() == 1
        container.flexOptions.size() == 0
        container.setPoints.size() == 1
    }

    def "An ExtInputContainer should extract set points data correctly"() {
        given:
        def container = new ExtInputContainer(0L)

        UUID primaryUuid = UUID.randomUUID()
        PValue primaryValue = new PValue(Quantities.getQuantity(10d, KILOWATT))
        container.addPrimaryValue(primaryUuid, primaryValue)

        UUID requester = UUID.randomUUID()
        UUID requested = UUID.randomUUID()
        container.addRequest(requester, [requested])

        UUID receiver = UUID.randomUUID()
        def flexOptions = new FlexOptions(UUID.randomUUID(), Quantities.getQuantity(0d, KILOWATT), Quantities.getQuantity(2d, KILOWATT), Quantities.getQuantity(5d, KILOWATT))
        container.addFlexOptions(receiver, [flexOptions])

        UUID emAsset = UUID.randomUUID()
        def setPoint = new PValue(Quantities.getQuantity(5d, KILOWATT))
        container.addSetPoint(emAsset, setPoint)

        when:
        def extracted = container.extractSetPoints()

        then:
        extracted.size() == 1
        extracted == [(emAsset): setPoint]

        container.primaryData.size() == 1
        container.flexRequests.size() == 1
        container.flexOptions.size() == 1
        container.setPoints.size() == 0
    }
}
