package edu.ie3.simona.api.data.results

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.simona.api.test.common.DataServiceTestData
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

class ExtResultPackageTest extends Specification implements DataServiceTestData {

    @Shared
    UUID nodeUuid = UUID.fromString("55b97041-64be-4e6b-983a-72dbde6eddf4")

    @Shared
    NodeResult nodeResult = new NodeResult(
            ZonedDateTime.parse("2020-01-30T17:26:44Z"),
            nodeUuid,
            Quantities.getQuantity(0.95, PowerSystemUnits.PU),
            Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)
    )

    def "ExtResultPackage should return voltage deviation correctly"() {
        given:
        def resultMap = Map.of(
                "Node", nodeResult
        )
        def extResultPackage = new ExtResultContainer(0L, resultMap)

        when:
        def calculatedVoltageDeviation = extResultPackage.getVoltageDeviation("Node")

        then:
        calculatedVoltageDeviation == -0.05d
    }

    def "ExtResultPackage should throw an exception, if voltage deviation was requested for a not NodeResult"() {
        given:
        def resultMap = Map.of(
                "Load", loadResult
        )
        def extResultPackage = new ExtResultContainer(0L, resultMap)

        when:
        extResultPackage.getVoltageDeviation("Load")

        then:
        thrown IllegalArgumentException
    }

    def "ExtResultPackage should return active power correctly"() {
        given:
        def resultMap = Map.of(
                "Load", loadResult
        )
        def extResultPackage = new ExtResultContainer(0L, resultMap)

        when:
        def returnedActivePower = extResultPackage.getActivePower("Load")

        then:
        returnedActivePower == 10d
    }

    def "ExtResultPackage should return reactive power correctly"() {
        given:
        def resultMap = Map.of(
                "Load", loadResult
        )
        def extResultPackage = new ExtResultContainer(0L, resultMap)

        when:
        def returnedReactivePower = extResultPackage.getReactivePower("Load")

        then:
        returnedReactivePower == 5d
    }

    def "ExtResultPackage should throw an exception, if active power was requested for a not SystemParticipantResult"() {
        given:
        def resultMap = Map.of(
                "Node", nodeResult
        )
        def extResultPackage = new ExtResultContainer(0L, resultMap)

        when:
        extResultPackage.getActivePower("Node")

        then:
        thrown IllegalArgumentException
    }
}