package edu.ie3.simona.api.simulation

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import edu.ie3.simona.api.data.ExtData
import edu.ie3.simona.api.simulation.ontology.ActivationMessage
import edu.ie3.simona.api.simulation.ontology.CompletionMessage
import edu.ie3.simona.api.simulation.ontology.ControlMessageToExt
import edu.ie3.simona.api.simulation.ontology.TerminationMessage
import edu.ie3.simona.api.simulation.ontology.TerminationCompleted
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Method

class ExtSimulationSpec extends Specification {

    @Shared
    ActorSystem actorSystem

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
            this.initReturnTick = initReturnTick
            this.activationReturnTick = activationReturnTick
        }

        @Override
        protected Long initialize() {
            return this.initReturnTick
        }

        @Override
        protected Optional<Long> doPreActivity(long tick) {
            return this.activationReturnTick
        }

        @Override
        protected Optional<Long> doPostActivity(long tick) {
            return this.activationReturnTick
        }

        @Override
        List<ExtData> getDataConnections() {
            return new ArrayList<ExtData>()
        }
    }

    def setupSpec() {
        actorSystem = ActorSystem.create()

        // setup private method
        handleMessage = ExtSimulation.getDeclaredMethod("takeAndHandleMessage", null)
        handleMessage.setAccessible(true)
    }

    def cleanupSpec() {
        TestKit.shutdownActorSystem(actorSystem)
        actorSystem = null
    }

    def "An ExtSimulation should handle initialization"() {
        given:
            def tick = -1L
            def newTick = 0L
            def extSimAdapter = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(Map.of(1, extSimAdapter.ref()), new String[0])
            def extSim = new TestSimulation(newTick, Optional.of(-2L))
            extSim.setup(extSimData, new ArrayList<ExtData>())

        when:
            extSimData.queueExtMsg(new ActivationMessage(tick, 1))
            def finishedActual = handleMessage.invoke(extSim)

        then:
            finishedActual == false
            extSimAdapter.expectMsg(new CompletionMessage(Optional.of(newTick), 1))
    }

    def "An ExtSimulation should handle activation and return given new triggers"() {
        given:
            def extSimAdapter = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(Map.of(1, extSimAdapter.ref()), new String[0])
            def newTickOpt = newTick.isEmpty() ?
                    Optional.<Long>empty() : Optional.of(newTick.first())
            def extSim = new TestSimulation(-2L, newTickOpt)
            extSim.setup(extSimData, new ArrayList<ExtData>())

        when:
            extSimData.queueExtMsg(new ActivationMessage(tick, 1))
            def finishedActual = handleMessage.invoke(extSim)

        then:
            finishedActual == finished
            extSimAdapter.expectMsg(new CompletionMessage(newTickOpt, 1))

        where:
            tick   | newTick       || finished
            0L     | [900L]        || false
            3600L  | [7200L]       || false
            7200L  | []            || true
            10800L | []            || true
    }

    def "An ExtSimulation throw an exception if a wrong phase was triggered"() {
        given:
            def tick = 0L
            def newTick = 900L
            def extSimAdapter = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(Map.of(1, extSimAdapter.ref()), new String[0])
            def extSim = new TestSimulation(tick, Optional.of(newTick))
            extSim.setup(extSimData, new ArrayList<ExtData>())

        when:
            extSimData.queueExtMsg(new ActivationMessage(tick, 0))
            handleMessage.invoke(extSim)

        then:
            Exception ex = thrown()
            // since we call a private method through reflection,
            // our expected exception is wrapped in an InvocationTargetException
            ex.getCause().getClass() == IllegalArgumentException
            extSimAdapter.expectNoMessage()
    }

    def "An ExtSimulation should handle termination properly"() {
        given:
            def extSimAdapter = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(Map.of(1, extSimAdapter.ref()), new String[0])
            def extSim = new TestSimulation(-1L, Optional.empty())
            extSim.setup(extSimData, new ArrayList<ExtData>())

        when:
            extSimData.queueExtMsg(new TerminationMessage(simlulationSuccessful, 1))
            def finishedActual = handleMessage.invoke(extSim)

        then:
            finishedActual == finished
            extSimAdapter.expectMsg(new TerminationCompleted(1))

        where:
            simlulationSuccessful || finished
            false                 || true
            true                  || true
    }

    class UnknownMessage implements ControlMessageToExt {}

    def "An ExtSimulation should handle unknown messages by throwing an exception"() {
        given:
            def extSimAdapter = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(Map.of(1, extSimAdapter.ref()), new String[0])
            def extSim = new TestSimulation(-1L, Optional.empty())
            extSim.setup(extSimData, new ArrayList<ExtData>())

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
