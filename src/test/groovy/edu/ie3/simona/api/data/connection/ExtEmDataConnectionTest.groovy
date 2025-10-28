package edu.ie3.simona.api.data.connection

import edu.ie3.simona.api.data.connection.ExtEmDataConnection.EmMode
import edu.ie3.simona.api.data.model.em.EmCommunicationMessage
import edu.ie3.simona.api.data.model.em.FlexOptionRequest
import edu.ie3.simona.api.data.model.em.PowerLimitFlexOptions
import edu.ie3.simona.api.ontology.DataMessageFromExt
import edu.ie3.simona.api.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.ontology.em.*
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtEmDataConnectionTest extends Specification implements DataServiceTestData {

    @Shared
    private ActorTestKit testKit

    @Shared
    private List<UUID> controlled = [inputUuid]

    def setupSpec() {
        testKit = ActorTestKit.create()
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

    def "ExtEmDataConnection should provide em data correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def emData = Map.of(inputUuid, new FlexOptionRequest(inputUuid, false))

        when:
        def wasSent = extEmDataConnection.sendEmData(0L, emData, log)

        then:
        wasSent
        dataService.expectMessage(new ProvideEmData(0, emData, [:], [:]))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))

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

        def emData = Map.of(inputUuid, new FlexOptionRequest(inputUuid, false))

        when:
        def wasSent = extEmDataConnection.sendEmData(0L, emData, [:], [:])

        then:
        wasSent
        dataService.expectMessage(new ProvideEmData(0, emData, [:], [:]))
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
        def wasSent = extEmDataConnection.sendEmData(0L, [:], [:], [:])

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

        def emData = Map.of(inputUuid, [new PowerLimitFlexOptions(inputUuid, inputUuid, power, power, power)])

        when:
        def wasSent = extEmDataConnection.sendEmData(0L, [:], emData, [:])

        then:
        wasSent
        dataService.expectMessage(new ProvideEmData(0, [:], emData, [:]))
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
        def wasSent = extEmDataConnection.sendEmData(0L, [:], [:], [:])

        then:
        !wasSent
        dataService.expectNoMessage()
    }

    def "ExtEmDataConnection should provide flex option requests correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        when:
        def wasSent = extEmDataConnection.sendFlexRequest(0L, [inputUuid], true)

        then:
        wasSent
        dataService.expectMessage( new ProvideEmData(0, [(inputUuid): new FlexOptionRequest(inputUuid, true)], [:], [:]))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEmDataConnection should provide communication messages correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(controlled, EmMode.BASE)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def message = new EmCommunicationMessage(UUID.randomUUID(), UUID.randomUUID(), null)

        when:
        def wasSent = extEmDataConnection.sendCommunicationMessage(0L, [message])

        then:
        wasSent
        dataService.expectMessage(new EmCommunicationMessages(0, [message]))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
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

        def sendMsg = new FlexOptionsResponse([(inputUuid): new PowerLimitFlexOptions(inputUuid, inputUuid, power, power, power)])

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extEmDataConnection.queueExtResponseMsg(sendMsg)

        def response = extEmDataConnection.requestEmFlexResults(0L, [inputUuid], false)

        then:
        dataService.expectMessage(new ProvideEmData(0L, [(inputUuid): new FlexOptionRequest(inputUuid, false)], [:], [:]))
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
