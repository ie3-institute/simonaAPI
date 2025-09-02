package edu.ie3.simona.api.data.container

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.system.FlexOptionsResult
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.simona.api.data.model.em.EmSetPoint
import edu.ie3.simona.api.data.model.em.FlexOptionRequest
import edu.ie3.simona.api.data.model.em.FlexOptions
import edu.ie3.simona.api.test.common.DataServiceTestData
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

class ExtOutputContainerTest extends Specification implements DataServiceTestData {

    @Shared
    UUID nodeUuid = UUID.fromString("55b97041-64be-4e6b-983a-72dbde6eddf4")

    @Shared
    NodeResult nodeResult = new NodeResult(
            ZonedDateTime.parse("2020-01-30T17:26:44Z"),
            nodeUuid,
            Quantities.getQuantity(0.95, PowerSystemUnits.PU),
            Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)
    )

    def "ExtResultContainer should add em data correctly"() {
        given:
        def container = new ExtOutputContainer(900L)

        UUID receiver1 = UUID.randomUUID()
        def setPoint = new EmSetPoint(receiver1, UUID.randomUUID())

        UUID receiver2 = UUID.randomUUID()
        def options = new FlexOptions(receiver2, UUID.randomUUID(), null, null, null)
        def options2 = new FlexOptions(receiver2, UUID.randomUUID(), null, null, null)

        when:
        container.addEmData(receiver1, setPoint)
        container.addEmData([(receiver2): [options]])
        container.addEmData(receiver2, options2)

        then:
        container.results.isEmpty()

        def allEmData = container.emData
        allEmData.size() == 2
        allEmData.get(receiver1) == [setPoint]
        allEmData.get(receiver2) == [options, options2]
    }

    def "ExtResultContainer should return all results correctly"() {
        given:
        def expected = [
                (nodeUuid): nodeResult,
                (inputUuid): loadResult
        ]

        def container = new ExtOutputContainer(0L)
        container.addResults(expected)

        expect:
        container.getResults() == expected
    }

    def "ExtResultContainer should return specific results correctly"() {
        given:
        def expected = [
                (nodeUuid): nodeResult,
                (inputUuid): loadResult
        ]

        def container = new ExtOutputContainer(0L)
        container.addResults(expected)

        when:
        def nodeResults = container.getResults(NodeResult)
        def loadResults = container.getResults(LoadResult)
        def flexOptionsResults = container.getResults(FlexOptionsResult)

        then:
        nodeResults == [(nodeUuid): nodeResult]
        loadResults == [(inputUuid): loadResult]
        flexOptionsResults == [:]
    }

    def "ExtResultContainer should return specific em data correctly"() {
        given:
        def container = new ExtOutputContainer(900L)

        UUID receiver1 = UUID.randomUUID()
        def setPoint = new EmSetPoint(receiver1, UUID.randomUUID())

        UUID receiver2 = UUID.randomUUID()
        def options = new FlexOptions(receiver2, UUID.randomUUID(), null, null, null)
        def options2 = new FlexOptions(receiver2, UUID.randomUUID(), null, null, null)

        def allEmData = [
                (receiver1): [setPoint],
                (receiver2): [options, options2]
        ]

        container.addEmData(allEmData)

        expect:
        container.getEmData(receiver1) == [setPoint]
        container.getEmData(receiver2) == [options, options2]
    }
}