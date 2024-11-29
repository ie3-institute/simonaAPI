package edu.ie3.simona.api.simulation

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.simona.api.data.ExtDataConnection
import edu.ie3.simona.api.simulation.mapping.DataType
import edu.ie3.simona.api.simulation.mapping.ExtEntityEntry
import edu.ie3.simona.api.simulation.mapping.ExtEntityMapping
import spock.lang.Shared
import spock.lang.Specification

class ExtCoSimulationTest extends Specification {

    @Shared
    ExtCoSimulation coSimulation


    def setupSpec() {
        coSimulation = new ExtCoSimulation("ExtCoSimulation", "") {
            @Override
            protected Long initialize() {
                return null
            }

            @Override
            protected Optional<Long> doActivity(long tick) {
                return null
            }

            @Override
            Set<ExtDataConnection> getDataConnections() {
                return null
            }
        }
    }

    def "An ExtCoSimulation can build a primary data connection correctly"() {
        given:
        UUID uuid1 = UUID.randomUUID()
        UUID uuid2 = UUID.randomUUID()
        UUID uuid3 = UUID.randomUUID()

        ExtEntityMapping mapping = new ExtEntityMapping([
                new ExtEntityEntry(uuid1, "primary1", ColumnScheme.ACTIVE_POWER, DataType.EXT_PRIMARY_INPUT),
                new ExtEntityEntry(uuid2, "em1", ColumnScheme.ACTIVE_POWER, DataType.EXT_EM_INPUT),
                new ExtEntityEntry(uuid3, "primary2", ColumnScheme.ACTIVE_POWER, DataType.EXT_PRIMARY_INPUT),
        ])

        when:
        def actual = coSimulation.buildPrimaryConnection(mapping)

        then:
        actual.getPrimaryDataAssets() == [uuid3, uuid1]
    }

    def "An ExtCoSimulation can build an em data connection correctly"() {
        given:
        UUID uuid1 = UUID.randomUUID()
        UUID uuid2 = UUID.randomUUID()
        UUID uuid3 = UUID.randomUUID()

        ExtEntityMapping mapping = new ExtEntityMapping([
                new ExtEntityEntry(uuid1, "em1", ColumnScheme.ACTIVE_POWER, DataType.EXT_EM_INPUT),
                new ExtEntityEntry(uuid2, "em2", ColumnScheme.ACTIVE_POWER, DataType.EXT_EM_INPUT),
                new ExtEntityEntry(uuid3, "primary1", ColumnScheme.ACTIVE_POWER, DataType.EXT_PRIMARY_INPUT),
        ])

        when:
        def actual = coSimulation.buildEmConnection(mapping)

        then:
        actual.getControlledEms() == [uuid1, uuid2]
    }

    def "An ExtCoSimulation can build a result data connection correctly"() {
        given:
        UUID uuid1 = UUID.randomUUID()
        UUID uuid2 = UUID.randomUUID()
        UUID uuid3 = UUID.randomUUID()

        ExtEntityMapping mapping = new ExtEntityMapping([
                new ExtEntityEntry(uuid1, "grid_result", ColumnScheme.ACTIVE_POWER, DataType.EXT_GRID_RESULT),
                new ExtEntityEntry(uuid2, "participant_result", ColumnScheme.ACTIVE_POWER, DataType.EXT_PARTICIPANT_RESULT),
                new ExtEntityEntry(uuid3, "primary1", ColumnScheme.ACTIVE_POWER, DataType.EXT_PRIMARY_INPUT),
        ])

        when:
        def actual = coSimulation.buildResultConnection(mapping)

        then:
        actual.getGridResultDataAssets() == [uuid1]
        actual.getParticipantResultDataAssets() == [uuid2]
    }
}
