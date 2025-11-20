package edu.ie3.simona.api.simulation

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.SValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.connection.ExtDataConnection
import edu.ie3.simona.api.data.connection.ExtEmDataConnection
import edu.ie3.simona.api.data.connection.ExtEmDataConnection.EmMode
import edu.ie3.simona.api.data.model.em.EmSetPoint
import edu.ie3.simona.api.exceptions.ExtDataConnectionException
import edu.ie3.simona.api.mapping.DataType
import edu.ie3.simona.api.mapping.ExtEntityMapping
import edu.ie3.simona.api.ontology.DataMessageFromExt
import edu.ie3.simona.api.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.ontology.em.ProvideEmData
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class ExtCoSimulationTest extends Specification {

    @Shared
    private static final Logger log = LoggerFactory.getLogger(ExtCoSimulationTest)

    @Shared
    private ExtCoSimulation sim

    @Shared
    private ActorTestKit testKit

    def setupSpec() {
        testKit = ActorTestKit.create()
        sim = new ExtCoSimulation("dummy", "dummy") {
            @Override
            protected Long initialize() {
                return 0L
            }

            @Override
            protected Optional<Long> doActivity(long tick) {
                return Optional.empty()
            }

            @Override
            Set<ExtDataConnection> getDataConnections() {
                return []
            }
        }
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

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

    def "An ExtCoSimulation throws an ExtDataConnectionException while trying to build an empty primary data connection"() {
        when:
        ExtCoSimulation.buildPrimaryConnection([:], log)

        then:
        ExtDataConnectionException ex = thrown(ExtDataConnectionException)
        ex.message == "The external data connection 'ExtPrimaryDataConnection' could not be build!"
    }

    def "An ExtCoSimulation can build an em data connection correctly"() {
        given:
        UUID uuid1 = UUID.randomUUID()
        UUID uuid2 = UUID.randomUUID()

        def controlled = [uuid1, uuid2]

        when:
        def actual = ExtCoSimulation.buildEmConnection(controlled, EmMode.BASE, log)

        then:
        actual.getControlledEms() == [uuid1, uuid2]
    }

    def "An ExtCoSimulation throws an ExtDataConnectionException while trying to build an empty em data connection"() {
        when:
        ExtCoSimulation.buildEmConnection([], EmMode.BASE, log)

        then:
        ExtDataConnectionException ex = thrown(ExtDataConnectionException)
        ex.message == "The external data connection 'ExtEmDataConnection' could not be build!"
    }

    def "An ExtCoSimulation can build a result data connection correctly"() {
        given:
        UUID uuid1 = UUID.fromString("e7209ff5-788a-4b1d-bb26-89b3e326ce74")
        UUID uuid2 = UUID.fromString("7ab84c3d-6c43-4c56-9257-21ef72e15b80")
        UUID uuid3 = UUID.fromString("806b0cba-23a6-43d0-821e-e023b6a90cc4")

        def mapping = new ExtEntityMapping([])
        mapping.includeIds(DataType.RESULT, [uuid1], Optional.empty())
        mapping.includeIds(DataType.EM, [uuid2], Optional.empty())
        mapping.includeIds(DataType.PRIMARY_RESULT, [uuid3], Optional.empty())

        when:
        def resultUuids = mapping.getAssets(DataType.RESULT)

        def actual = ExtCoSimulation.buildResultConnection(resultUuids, log)

        then:
        actual.resultUuids == [uuid1, uuid3]
    }

    def "An ExtCoSimulation throws an ExtDataConnectionException while trying to build an empty result data connection"() {
        when:
        ExtCoSimulation.buildResultConnection([], log)

        then:
        ExtDataConnectionException ex = thrown(ExtDataConnectionException)
        ex.message == "The external data connection 'ExtResultDataConnection' could not be build!"
    }

}
