package edu.ie3.simona.api.simulation

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import edu.ie3.simona.api.data.ExtData
import edu.ie3.simona.api.simulation.ontology.ActivityStartTrigger
import edu.ie3.simona.api.simulation.ontology.CompletionMessage
import edu.ie3.simona.api.simulation.ontology.ExtSimMessage
import edu.ie3.simona.api.simulation.ontology.Terminate
import edu.ie3.simona.api.simulation.ontology.TerminationCompleted
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Method

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

class ExtSimulationSpec extends Specification {

    @Shared
    ActorSystem actorSystem

    @Shared
    Method handleMessage

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
            def newTicks = [0L]
            def testProbe = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(testProbe.ref(), new String[0])
            def extSim = mock(ExtSimulation)
            extSim.setup(extSimData, new ArrayList<ExtData>())

            when(extSim.initialize()).thenReturn(newTicks)

        when:
            extSimData.queueExtMsg(new ActivityStartTrigger(tick))
            def finishedActual = handleMessage.invoke(extSim)

        then:
            finishedActual == false
            testProbe.expectMsg(new CompletionMessage(newTicks))
    }

    def "An ExtSimulation should handle activation and return given new triggers"() {
        given:
            def testProbe = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(testProbe.ref(), new String[0])
            def extSim = mock(ExtSimulation)
            extSim.setup(extSimData, new ArrayList<ExtData>())

            when(extSim.doActivity(tick)).thenReturn(newTicks)

        when:
            extSimData.queueExtMsg(new ActivityStartTrigger(tick))
            def finishedActual = handleMessage.invoke(extSim)

        then:
            finishedActual == finished
            testProbe.expectMsg(new CompletionMessage(newTicks))

        where:
            tick   | newTicks      || finished
            -1L    | [450L]        || false
            0L     | [900L, 1800L] || false
            3600L  | [7200L]       || false
            7200L  | []            || true
            10800L | []            || true
    }

    def "An ExtSimulation should handle termination properly"() {
        given:
            def testProbe = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(testProbe.ref(), new String[0])
            def extSim = mock(ExtSimulation)
            extSim.setup(extSimData, new ArrayList<ExtData>())

        when:
            extSimData.queueExtMsg(new Terminate(simlulationSuccessful))
            def finishedActual = handleMessage.invoke(extSim)

        then:
            finishedActual == finished
            testProbe.expectMsg(new TerminationCompleted())
            verify(extSim).terminate(simlulationSuccessful) // terminate() has been called exactly once

        where:
            simlulationSuccessful || finished
            false                 || true
            true                  || true
    }

    class UnknownMessage implements ExtSimMessage {}

    def "An ExtSimulation should handle unknown messages by throwing an exception"() {
        given:
            def testProbe = new TestProbe(actorSystem)
            def extSimData = new ExtSimAdapterData(testProbe.ref(), new String[0])
            def extSim = mock(ExtSimulation)
            extSim.setup(extSimData, new ArrayList<ExtData>())

        when:
            extSimData.queueExtMsg(new UnknownMessage())
            handleMessage.invoke(extSim)

        then:
            Exception ex = thrown()
            // since we call a private method through reflection,
            // our expected exception is wrapped in an InvocationTargetException
            ex.getCause().getClass() == IllegalArgumentException
            testProbe.expectNoMessage()
    }
}
