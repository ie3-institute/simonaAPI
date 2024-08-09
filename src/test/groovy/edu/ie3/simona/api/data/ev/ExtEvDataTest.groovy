package edu.ie3.simona.api.data.ev

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import edu.ie3.simona.api.data.ev.model.EvModel
import edu.ie3.simona.api.data.ev.ontology.ProvideArrivingEvs
import edu.ie3.simona.api.data.ev.ontology.ProvideCurrentPrices
import edu.ie3.simona.api.data.ev.ontology.ProvideDepartingEvs
import edu.ie3.simona.api.data.ev.ontology.ProvideEvcsFreeLots
import edu.ie3.simona.api.data.ev.ontology.RequestCurrentPrices
import edu.ie3.simona.api.data.ev.ontology.RequestDepartingEvs
import edu.ie3.simona.api.data.ev.ontology.RequestEvcsFreeLots
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import spock.lang.Shared
import spock.lang.Specification

class ExtEvDataTest extends Specification {

    @Shared
    ActorSystem actorSystem

    def setupSpec() {
        actorSystem = ActorSystem.create()
    }

    def cleanupSpec() {
        TestKit.shutdownActorSystem(actorSystem)
        actorSystem = null
    }

    def "ExtEvData should request and receive free evcs lots correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extEvData = new ExtEvData(dataService.ref(), extSimAdapter.ref())

        def sentMsg = new ProvideEvcsFreeLots()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEvData.queueExtResponseMsg(sentMsg)
        def actualReceivedEvcs = extEvData.requestAvailablePublicEvcs()

        then:
        dataService.expectMsg(new RequestEvcsFreeLots())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        actualReceivedEvcs == sentMsg.evcs()
    }

    def "ExtEvData should request and receive current charging prices correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extEvData = new ExtEvData(dataService.ref(), extSimAdapter.ref())

        def sentMsg = new ProvideCurrentPrices()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEvData.queueExtResponseMsg(sentMsg)
        def actualReceivedPrices = extEvData.requestCurrentPrices()

        then:
        dataService.expectMsg(new RequestCurrentPrices())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        actualReceivedPrices == sentMsg.prices()
    }

    def "ExtEvData should request and receive departing EVs correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extEvData = new ExtEvData(dataService.ref(), extSimAdapter.ref())

        def requestedDepartingEvs = new HashMap<UUID, List<UUID>>()
        requestedDepartingEvs.put(UUID.randomUUID(), new ArrayList<UUID>())
        def sentMsg = new ProvideDepartingEvs()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEvData.queueExtResponseMsg(sentMsg)
        def actualReceivedEvs = extEvData.requestDepartingEvs(requestedDepartingEvs)

        then:
        dataService.expectMsg(new RequestDepartingEvs(requestedDepartingEvs))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        actualReceivedEvs == sentMsg.departedEvs()
    }

    def "ExtEvData should provide arriving EVs correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extEvData = new ExtEvData(dataService.ref(), extSimAdapter.ref())

        def arrivingEvs = new HashMap<UUID, List<EvModel>>()
        arrivingEvs.put(UUID.randomUUID(), new ArrayList<EvModel>())

        when:
        extEvData.provideArrivingEvs(arrivingEvs, Optional.of(60L))

        then:
        dataService.expectMsg(new ProvideArrivingEvs(arrivingEvs, Optional.of(60L)))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEvData should fail if wrong response is sent"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extEvData = new ExtEvData(dataService.ref(), extSimAdapter.ref())

        def unexpectedMsg = new ProvideCurrentPrices()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEvData.queueExtResponseMsg(unexpectedMsg)
        extEvData.requestAvailablePublicEvcs()

        then:
        dataService.expectMsg(new RequestEvcsFreeLots())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        thrown RuntimeException
    }
}
