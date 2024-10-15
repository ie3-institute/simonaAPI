package edu.ie3.simona.api.data.results

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

class ExtResultPackageTest extends Specification {
    @Shared
    UUID loadUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")

    @Shared
    UUID nodeUuid = UUID.fromString("55b97041-64be-4e6b-983a-72dbde6eddf4")

    @Shared
    NodeResult nodeResult = new NodeResult(
            ZonedDateTime.parse("2020-01-30T17:26:44Z"),
            nodeUuid,
            Quantities.getQuantity(0.95, PowerSystemUnits.PU),
            Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)
    )

    @Shared
    LoadResult loadResult = new LoadResult(
            ZonedDateTime.parse("2020-01-30T17:26:44Z"),
            loadUuid,
            Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN),
            Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
    )

    def "ExtResultPackage should return voltage deviation correctly"() {
        given:
        def resultMap = Map.of(
                "Node", nodeResult
        )
        def extResultPackage = new ExtResultPackage(0L, resultMap)

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
        def extResultPackage = new ExtResultPackage(0L, resultMap)

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
        def extResultPackage = new ExtResultPackage(0L, resultMap)

        when:
        def returnedActivePower = extResultPackage.getActivePower("Load")

        then:
        returnedActivePower == 10d
    }

    def "ExtResultPackage should throw an exception, if active power was requested for a not SystemParticipantResult"() {
        given:
        def resultMap = Map.of(
                "Node", nodeResult
        )
        def extResultPackage = new ExtResultPackage(0L, resultMap)

        when:
        extResultPackage.getActivePower("Node")

        then:
        thrown IllegalArgumentException
    }
}