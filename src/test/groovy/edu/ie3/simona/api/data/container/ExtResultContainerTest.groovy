package edu.ie3.simona.api.data.container

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.system.FlexOptionsResult
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.simona.api.test.common.DataServiceTestData
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

class ExtResultContainerTest extends Specification implements DataServiceTestData {

    @Shared
    UUID nodeUuid = UUID.fromString("55b97041-64be-4e6b-983a-72dbde6eddf4")

    @Shared
    NodeResult nodeResult = new NodeResult(
            ZonedDateTime.parse("2020-01-30T17:26:44Z"),
            nodeUuid,
            Quantities.getQuantity(0.95, PowerSystemUnits.PU),
            Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)
    )

    def "ExtResultContainer should return all results correctly"() {
        given:
        def expected = [
                (nodeUuid): nodeResult,
                (inputUuid): loadResult
        ]

        def container = new ExtResultContainer(0L, expected)

        expect:
        container.getResults() == expected
    }

    def "ExtResultContainer should return specific results correctly"() {
        given:
        def expected = [
                (nodeUuid): nodeResult,
                (inputUuid): loadResult
        ]

        def container = new ExtResultContainer(0L, expected)

        when:
        def nodeResults = container.getResults(NodeResult)
        def loadResults = container.getResults(LoadResult)
        def flexOptionsResults = container.getResults(FlexOptionsResult)

        then:
        nodeResults == [(nodeUuid): nodeResult]
        loadResults == [(inputUuid): loadResult]
        flexOptionsResults == [:]
    }
}