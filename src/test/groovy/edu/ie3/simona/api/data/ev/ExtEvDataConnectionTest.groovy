package edu.ie3.simona.api.data.ev

import edu.ie3.simona.api.data.ev.model.EvModel
import edu.ie3.simona.api.data.ev.ontology.*
import edu.ie3.simona.api.data.ontology.DataMessageFromExt
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtEvDataConnectionTest extends Specification {

    @Shared
    ActorTestKit testKit

    def setupSpec() {
        testKit = ActorTestKit.create()
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

    def "ExtEvDataConnection should request and receive free evcs lots correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEvDataConnection = new ExtEvDataConnection()
        extEvDataConnection.setActorRefs(dataService.ref(), extSimAdapter.ref())

        def sentMsg = new ProvideEvcsFreeLots()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEvDataConnection.queueExtResponseMsg(sentMsg)
        def actualReceivedEvcs = extEvDataConnection.requestAvailablePublicEvcs()

        then:
        dataService.expectMessage(new RequestEvcsFreeLots())
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
        actualReceivedEvcs == sentMsg.evcs()
    }

    def "ExtEvDataConnection should request and receive current charging prices correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEvDataConnection = new ExtEvDataConnection()
        extEvDataConnection.setActorRefs(dataService.ref(), extSimAdapter.ref())

        def sentMsg = new ProvideCurrentPrices()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEvDataConnection.queueExtResponseMsg(sentMsg)
        def actualReceivedPrices = extEvDataConnection.requestCurrentPrices()

        then:
        dataService.expectMessage(new RequestCurrentPrices())
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
        actualReceivedPrices == sentMsg.prices()
    }

    def "ExtEvDataConnection should request and receive departing EVs correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEvDataConnection = new ExtEvDataConnection()
        extEvDataConnection.setActorRefs(dataService.ref(), extSimAdapter.ref())

        def requestedDepartingEvs = new HashMap<UUID, List<UUID>>()
        requestedDepartingEvs.put(UUID.randomUUID(), new ArrayList<UUID>())
        def sentMsg = new ProvideDepartingEvs()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEvDataConnection.queueExtResponseMsg(sentMsg)
        def actualReceivedEvs = extEvDataConnection.requestDepartingEvs(requestedDepartingEvs)

        then:
        dataService.expectMessage(new RequestDepartingEvs(requestedDepartingEvs))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
        actualReceivedEvs == sentMsg.departedEvs()
    }

    def "ExtEvDataConnection should provide arriving EVs correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEvDataConnection = new ExtEvDataConnection()
        extEvDataConnection.setActorRefs(dataService.ref(), extSimAdapter.ref())

        def arrivingEvs = new HashMap<UUID, List<EvModel>>()
        arrivingEvs.put(UUID.randomUUID(), new ArrayList<EvModel>())

        when:
        extEvDataConnection.provideArrivingEvs(arrivingEvs, Optional.of(60L))

        then:
        dataService.expectMessage(new ProvideArrivingEvs(arrivingEvs, Optional.of(60L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEvDataConnection should fail if wrong response is sent"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEvDataConnection = new ExtEvDataConnection()
        extEvDataConnection.setActorRefs(dataService.ref(), extSimAdapter.ref())

        def unexpectedMsg = new ProvideCurrentPrices()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEvDataConnection.queueExtResponseMsg(unexpectedMsg)
        extEvDataConnection.requestAvailablePublicEvcs()

        then:
        dataService.expectMessage(new RequestEvcsFreeLots())
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
        thrown RuntimeException
    }
}
