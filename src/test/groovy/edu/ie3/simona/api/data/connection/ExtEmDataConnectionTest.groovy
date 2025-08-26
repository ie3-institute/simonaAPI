package edu.ie3.simona.api.data.connection

import edu.ie3.simona.api.data.connection.ExtEmDataConnection.EmMode
import edu.ie3.simona.api.data.model.em.ExtendedFlexOptionsResult
import edu.ie3.simona.api.data.model.em.FlexOptionRequest
import edu.ie3.simona.api.data.model.em.FlexOptions
import edu.ie3.simona.api.ontology.DataMessageFromExt
import edu.ie3.simona.api.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.ontology.em.*
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime

class ExtEmDataConnectionTest extends Specification implements DataServiceTestData {

    @Shared
    ActorTestKit testKit

    @Shared
    private UUID sender = UUID.randomUUID()

    @Shared
    private List<UUID> controlled = [inputUuid]

    def setupSpec() {
        testKit = ActorTestKit.create()
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

    def "ExtEmDataConnection should provide em flex requests correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def emData = Map.of(inputUuid, new FlexOptionRequest(inputUuid, sender))

        when:
        extEmDataConnection.sendFlexRequests(0L, emData, Optional.of(900L), log)

        then:
        dataService.expectMessage(new ProvideFlexRequest(0, emData, Optional.of(900L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEmDataConnection should send no message, if no flex requests are given"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = [:] as Map

        when:
        extEmDataConnection.sendFlexRequests(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectNoMessage()
    }

    def "ExtEmDataConnection should provide em flex options correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def emData = Map.of(inputUuid, [new FlexOptions(inputUuid, UUID.randomUUID(), power, power, power)])

        when:
        extEmDataConnection.sendFlexOptions(0L, emData, Optional.of(900L), log)

        then:
        dataService.expectMessage(new ProvideEmFlexOption(0, emData, Optional.of(900L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEmDataConnection should send no message, if no flex options are given"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = [:] as Map

        when:
        extEmDataConnection.sendFlexRequests(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectNoMessage()
    }

    def "ExtEmDataConnection should send no message, if input data is empty"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = [:] as Map

        when:
        extEmDataConnection.sendSetPoints(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectNoMessage()
    }

    def "ExtEmDataConnection should send no message, if no em set points are given"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = [:] as Map

        when:
        extEmDataConnection.sendSetPoints(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectNoMessage()
    }

    def "ExtEmDataConnection should request and receive flex options correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def sendMsg = new FlexOptionsResponse([(inputUuid): new ExtendedFlexOptionsResult(ZonedDateTime.now(), inputUuid, UUID.randomUUID(), power, power, power)])

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEmDataConnection.queueExtResponseMsg(sendMsg)

        def response = extEmDataConnection.requestEmFlexResults(0L, [inputUuid], false)

        then:
        dataService.expectMessage(new RequestEmFlexResults(0L, [inputUuid], false))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
        response == sendMsg.receiverToFlexOptions()
    }

    def "ExtEmDataConnection should request and receive flex completion correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def sendMsg = new EmCompletion(Optional.of(900L))

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEmDataConnection.queueExtResponseMsg(sendMsg)

        def response = extEmDataConnection.requestCompletion(0L)

        then:
        dataService.expectMessage(new RequestEmCompletion(0L))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
        response == sendMsg.maybeNextTick()
    }

}
