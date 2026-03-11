package edu.ie3.simona.api.simulation

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.SValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.connection.ExtDataConnection
import edu.ie3.simona.api.data.connection.ExtEmDataConnection.EmMode
import edu.ie3.simona.api.data.container.ExtInputContainer
import edu.ie3.simona.api.data.container.ExtOutputContainer
import edu.ie3.simona.api.exceptions.ExtDataConnectionException
import edu.ie3.simona.api.mapping.DataType
import edu.ie3.simona.api.mapping.ExtEntityMapping
import edu.ie3.simona.api.simulation.ExtCoSimFramework.Status
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class ExtCoSimulationTest extends Specification {

    @Shared
    private static final Logger log = LoggerFactory.getLogger(ExtCoSimulationTest)

    @Shared
    private ActorTestKit testKit

    def setupSpec() {
        testKit = ActorTestKit.create()
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

    /**
     * Test simulation.
     */
    private class TestSimulation extends ExtCoSimulation<ExtCoSimFramework.InitData> {

        TestSimulation(ExtCoSimFramework framework) {
            super("TestSimulation", framework)
        }

        @Override
        protected long initialize() {
            return 0L
        }


        @Override
        Set<ExtDataConnection> getDataConnections() {
            return []
        }

        @Override
        ExtOutputContainer handleExternalData(ExtInputContainer inputData) throws Exception {
            return new ExtOutputContainer(inputData.getTick(), inputData.getMaybeNextTick())
        }

        @Override
        ExtOutputContainer handleNoExternalData(long tick) throws Exception {
            return new ExtOutputContainer(tick, OptionalLong.of(tick + 900))
        }

        @Override
        void finishSimulation(long tick) throws Exception {

        }

        @Override
        long determineNextTick(long tick) {
            return 0
        }

        @Override
        boolean continueActivity(long tick) {
            return false
        }
    }

    private static ExtCoSimFramework buildDummyFramework(Status returnStatus) {
        return new ExtCoSimFramework() {
            @Override
            String getName() {
                return "dummy"
            }

            @Override
            void setInitDataQueue(Queue initDataQueue) {

            }

            @Override
            Status getStatus(long simonaTick) throws InterruptedException {
                return returnStatus
            }

            @Override
            void provideOutputData(ExtOutputContainer outputData) {

            }

            @Override
            void goToNextTick(long simonaTick) {

            }
        }
    }

    def "An ExtCoSimulation should handle HasData status correctly"() {
        given:
        def inputData = new ExtInputContainer(0L, 900L)

        def framework = buildDummyFramework(new ExtCoSimFramework.HasData(inputData))

        def extSim = new TestSimulation(framework)

        when:
        def maybeNextTick = extSim.doActivity(0L)

        then:
        maybeNextTick == OptionalLong.of(900L)
    }

    def "An ExtCoSimulation should handle SimonaIsBehind status correctly"() {
        given:
        def framework = buildDummyFramework(new ExtCoSimFramework.SimonaIsBehind(800L))

        def extSim = new TestSimulation(framework)

        when:
        def maybeNextTick = extSim.doActivity(0L)

        then:
        maybeNextTick == OptionalLong.of(800L)
    }

    def "An ExtCoSimulation should handle SimonaIsAhead status correctly"() {
        given:
        def framework = buildDummyFramework(new ExtCoSimFramework.SimonaIsAhead())

        def extSim = new TestSimulation(framework)

        when:
        def maybeNextTick = extSim.doActivity(900L)

        then:
        maybeNextTick == OptionalLong.empty()
    }

    def "An ExtCoSimulation should handle Finished status correctly"() {
        given:
        def framework = buildDummyFramework(new ExtCoSimFramework.Finished())

        def extSim = new TestSimulation(framework)

        when:
        def maybeNextTick = extSim.doActivity(0L)

        then:
        maybeNextTick == OptionalLong.empty()
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
