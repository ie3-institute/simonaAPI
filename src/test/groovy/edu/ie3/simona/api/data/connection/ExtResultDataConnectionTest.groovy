package edu.ie3.simona.api.data.connection

import edu.ie3.simona.api.ontology.DataMessageFromExt
import edu.ie3.simona.api.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.ontology.results.ProvideResultEntities
import edu.ie3.simona.api.ontology.results.RequestResultEntities
import edu.ie3.simona.api.ontology.results.ResultDataResponseMessageToExt
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtResultDataConnectionTest extends Specification implements DataServiceTestData {

    @Shared
    ActorTestKit testKit

    @Shared
    List<UUID> resultEntities = [inputUuid]

    class WrongResultDataResponseMessageToExt implements ResultDataResponseMessageToExt {}

    def setupSpec() {
        testKit = ActorTestKit.create()
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

    def "ExtResultsData should request and receive results correctly as ModelResultEntity"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extResultDataConnection = new ExtResultDataConnection(resultEntities)
        extResultDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def sentMsg = new ProvideResultEntities([loadResult])

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultDataConnection.queueExtResponseMsg(sentMsg)
        def receivedResults = extResultDataConnection.requestResults(0L)

        then:
        dataService.expectMessage(new RequestResultEntities(0L, [inputUuid]))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
        receivedResults.get(inputUuid) == loadResult
    }

    def "ExtResultsData should fail if wrong response is sent"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extResultDataConnection = new ExtResultDataConnection(resultEntities)
        extResultDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def unexpectedMsg = new WrongResultDataResponseMessageToExt()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultDataConnection.queueExtResponseMsg(unexpectedMsg)
        extResultDataConnection.requestResults(0L)

        then:
        dataService.expectMessage(new RequestResultEntities(0L, [inputUuid]))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
        thrown RuntimeException
    }

    def "ExtResultData should convert a list of result entities correctly to a map of resultAssetMappingId to result entity"() {
        given:
        def extResultDataConnection = new ExtResultDataConnection(resultEntities)

        when:
        def mapOfResults = extResultDataConnection.createResultMap([loadResult])

        then:
        mapOfResults.size() == 1
        mapOfResults.get(inputUuid) == loadResult
    }
}