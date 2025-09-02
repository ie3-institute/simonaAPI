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
    List<UUID> controlled = [inputUuid]

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

        def emData = Map.of(inputUuid, new FlexOptionRequest(inputUuid, null, false))

        when:
        def wasSent = extEmDataConnection.sendEmData(0L, emData, [:], [:], Optional.of(900L))

        then:
        wasSent
        dataService.expectMessage(new ProvideEmData(0, emData, [:], [:], Optional.of(900L)))
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

        when:
        def wasSent = extEmDataConnection.sendEmData(0L, [:], [:], [:], Optional.of(900L))

        then:
        !wasSent
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
        def wasSent = extEmDataConnection.sendEmData(0L, [:], emData, [:], Optional.of(900L))

        then:
        wasSent
        dataService.expectMessage(new ProvideEmData(0, [:], emData, [:], Optional.of(900L)))
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

        when:
        def wasSent = extEmDataConnection.sendEmData(0L, [:], [:], [:], Optional.of(900L))

        then:
        !wasSent
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
        def wasSent = extEmDataConnection.sendSetPoints(0L, inputDataMap, Optional.of(900L))

        then:
        !wasSent
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
        def wasSent = extEmDataConnection.sendSetPoints(0L, inputDataMap, Optional.of(900L))

        then:
        !wasSent
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

        // we request tick 1000 as next tick
        def response = extEmDataConnection.requestCompletion(0L, 1000L)

        then:
        dataService.expectMessage(new RequestEmCompletion(0L, Optional.of(1000L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))

        // we received tick 900 as next, because we need an activation for this tick
        response == Optional.of(900L)
        response == sendMsg.maybeNextTick()
    }

}
