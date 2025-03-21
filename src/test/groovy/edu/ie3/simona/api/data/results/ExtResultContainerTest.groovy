package edu.ie3.simona.api.data.results

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.simona.api.data.datacontainer.ExtResultContainer
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

    def "ExtResultContainer should return voltage deviation correctly"() {
        given:
        def resultMap = Map.of(
                "Node", nodeResult
        )
        def extResultContainer = new ExtResultContainer(0L, resultMap)

        when:
        def calculatedVoltageDeviation = extResultContainer.getVoltageDeviation("Node")

        then:
        calculatedVoltageDeviation == -0.05d
    }

    def "ExtResultContainer should throw an exception, if voltage deviation was requested for a not NodeResult"() {
        given:
        def resultMap = Map.of(
                "Load", loadResult
        )
        def extResultContainer = new ExtResultContainer(0L, resultMap)

        when:
        extResultContainer.getVoltageDeviation("Load")

        then:
        thrown IllegalArgumentException
    }

    def "ExtResultContainer should return active power correctly"() {
        given:
        def resultMap = Map.of(
                "Load", loadResult
        )
        def extResultContainer = new ExtResultContainer(0L, resultMap)

        when:
        def returnedActivePower = extResultContainer.getActivePower("Load")

        then:
        returnedActivePower == 10d
    }

    def "ExtResultContainer should return reactive power correctly"() {
        given:
        def resultMap = Map.of(
                "Load", loadResult
        )
        def extResultContainer = new ExtResultContainer(0L, resultMap)

        when:
        def returnedReactivePower = extResultContainer.getReactivePower("Load")

        then:
        returnedReactivePower == 5d
    }

    def "ExtResultContainer should throw an exception, if active power was requested for a not SystemParticipantResult"() {
        given:
        def resultMap = Map.of(
                "Node", nodeResult
        )
        def extResultContainer = new ExtResultContainer(0L, resultMap)

        when:
        extResultContainer.getActivePower("Node")

        then:
        thrown IllegalArgumentException
    }
}