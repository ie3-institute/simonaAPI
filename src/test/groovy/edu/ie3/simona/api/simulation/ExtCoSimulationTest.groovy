package edu.ie3.simona.api.simulation

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.SValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.connection.ExtEmDataConnection.EmMode
import edu.ie3.simona.api.mapping.DataType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class ExtCoSimulationTest extends Specification {

    @Shared
    private static final Logger log = LoggerFactory.getLogger(ExtCoSimulationTest)

    def "An ExtCoSimulation can build a primary data connection correctly"() {
        given:
        UUID uuid1 = UUID.randomUUID()
        UUID uuid2 = UUID.randomUUID()

        Map<UUID, Class<Value>> assetsToClasses = [
                (uuid1): PValue,
                (uuid2): SValue
        ] as Map

        when:
        def actual = ExtCoSimulation.buildPrimaryConnection(assetsToClasses, log)

        then:
        actual.getPrimaryDataAssets() == [uuid1, uuid2]
    }

    def "An ExtCoSimulation can build an em data connection correctly"() {
        given:
        UUID uuid1 = UUID.randomUUID()
        UUID uuid2 = UUID.randomUUID()

        def controlled = [uuid1, uuid2]

        when:
        def actual = ExtCoSimulation.buildEmConnection(controlled, EmMode.SET_POINT, log)

        then:
        actual.getControlledEms() == [uuid1, uuid2]
    }

    def "An ExtCoSimulation can build a result data connection correctly"() {
        given:
        UUID uuid1 = UUID.randomUUID()
        UUID uuid2 = UUID.randomUUID()
        UUID uuid3 = UUID.randomUUID()

        def mapping = [
                (DataType.EXT_GRID_RESULT)       : [uuid1],
                (DataType.EXT_PARTICIPANT_RESULT): [uuid2],
                (DataType.EXT_FLEX_OPTIONS_RESULT): [uuid3]
        ]

        when:
        def actual = ExtCoSimulation.buildResultConnection(mapping, log)

        then:
        actual.getGridResultDataAssets() == [uuid1]
        actual.getParticipantResultDataAssets() == [uuid2]
    }
}
