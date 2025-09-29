package edu.ie3.simona.api.simulation

import edu.ie3.simona.api.data.ExtSimAdapterData
import edu.ie3.simona.api.data.connection.ExtDataConnection
import edu.ie3.simona.api.ontology.simulation.ActivationMessage
import edu.ie3.simona.api.ontology.simulation.CompletionMessage
import edu.ie3.simona.api.ontology.simulation.ControlMessageToExt
import edu.ie3.simona.api.ontology.simulation.ControlResponseMessageFromExt
import edu.ie3.simona.api.ontology.simulation.TerminationCompleted
import edu.ie3.simona.api.ontology.simulation.TerminationMessage
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Method

class ExtSimulationSpec extends Specification {

    @Shared
    ActorTestKit testKit

    @Shared
    Method handleMessage

    /**
     * Test simulation that distinguishes between returned ticks
     * after initialization and regular activation
     */
    private class TestSimulation extends ExtSimulation {

        private Long initReturnTick
        private Optional<Long> activationReturnTick

        TestSimulation(Long initReturnTick, Optional<Long> activationReturnTick) {
            super("TestSimulation")
            this.initReturnTick = initReturnTick
            this.activationReturnTick = activationReturnTick
        }

        @Override
        protected Long initialize() {
            return this.initReturnTick
        }

        @Override
        protected Optional<Long> doActivity(long tick) {
            return this.activationReturnTick
        }

        @Override
        Set<ExtDataConnection> getDataConnections() {
            return []
        }
    }

    def setupSpec() {
        testKit = ActorTestKit.create()

        // setup private method
        handleMessage = ExtSimulation.getDeclaredMethod("takeAndHandleMessage", null)
        handleMessage.setAccessible(true)
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

    def "An ExtSimulation should handle initialization"() {
        given:
        def tick = -1L
        def newTick = 0L
        def extSimAdapter = testKit.createTestProbe(ControlResponseMessageFromExt)
        def extSimData = new ExtSimAdapterData(extSimAdapter.ref(), new String[0], null, null)
        def extSim = new TestSimulation(newTick, Optional.of(-2L))
        extSim.setAdapterData(extSimData)

        when:
        extSimData.queueExtMsg(new ActivationMessage(tick))
        def finishedActual = handleMessage.invoke(extSim)

        then:
        finishedActual == false
        extSimAdapter.expectMessage(new CompletionMessage(Optional.of(newTick)))
    }

    def "An ExtSimulation should handle activation and return given new triggers"() {
        given:
        def extSimAdapter = testKit.createTestProbe(ControlResponseMessageFromExt)
        def extSimData = new ExtSimAdapterData(extSimAdapter.ref(), new String[0], null, null)
        def newTickOpt = newTick.isEmpty() ?
                    Optional.<Long>empty() : Optional.of(newTick.first())
        def extSim = new TestSimulation(-2L, newTickOpt)
        extSim.setAdapterData(extSimData)

        when:
        extSimData.queueExtMsg(new ActivationMessage(tick))
        def finishedActual = handleMessage.invoke(extSim)

        then:
        finishedActual == finished
        extSimAdapter.expectMessage(new CompletionMessage(newTickOpt))

        where:
        tick   | newTick       || finished
        0L     | [900L]        || false
        3600L  | [7200L]       || false
        7200L  | []            || true
        10800L | []            || true
    }

    def "An ExtSimulation should handle termination properly"() {
        given:
        def extSimAdapter = testKit.createTestProbe(ControlResponseMessageFromExt)
        def extSimData = new ExtSimAdapterData(extSimAdapter.ref(), new String[0], null, null)
        def extSim = new TestSimulation(-1L, Optional.empty())
        extSim.setAdapterData(extSimData)

        when:
        extSimData.queueExtMsg(new TerminationMessage(simlulationSuccessful))
        def finishedActual = handleMessage.invoke(extSim)

        then:
        finishedActual == finished
        extSimAdapter.expectMessage(new TerminationCompleted())

        where:
        simlulationSuccessful || finished
        false                 || true
        true                  || true
    }

    class UnknownMessage implements ControlMessageToExt {}

    def "An ExtSimulation should handle unknown messages by throwing an exception"() {
        given:
        def extSimAdapter = testKit.createTestProbe(ControlResponseMessageFromExt)
        def extSimData = new ExtSimAdapterData(extSimAdapter.ref(), new String[0], null, null)
        def extSim = new TestSimulation(-1L, Optional.empty())
        extSim.setAdapterData(extSimData)

        when:
        extSimData.queueExtMsg(new UnknownMessage())
        handleMessage.invoke(extSim)

        then:
        Exception ex = thrown()
        // since we call a private method through reflection,
        // our expected exception is wrapped in an InvocationTargetException
        ex.getCause().getClass() == IllegalArgumentException
        extSimAdapter.expectNoMessage()
    }
}
