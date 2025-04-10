package edu.ie3.simona.api.data.connection

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.simona.api.ontology.DataMessageFromExt
import edu.ie3.simona.api.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.ontology.result.ProvideResultEntities
import edu.ie3.simona.api.ontology.result.RequestResultEntities
import edu.ie3.simona.api.ontology.result.ResultDataResponseMessageToExt
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Angle
import javax.measure.quantity.ElectricCurrent
import java.time.ZonedDateTime

class ExtResultDataConnectionTest extends Specification implements DataServiceTestData {

    @Shared
    ActorTestKit testKit

    @Shared
    List<UUID> participantResultAssets = [inputUuid]

    @Shared
    List<UUID> gridResultAssets = []

    @Shared
    List<UUID> flexResultAssets = []

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
        def extResultDataConnection = new ExtResultDataConnection(participantResultAssets, gridResultAssets, flexResultAssets)
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
        def extResultDataConnection = new ExtResultDataConnection(participantResultAssets, gridResultAssets, flexResultAssets)
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
        def extResultDataConnection = new ExtResultDataConnection(participantResultAssets, gridResultAssets, flexResultAssets)

        when:
        def mapOfResults = extResultDataConnection.createResultMap([loadResult])

        then:
        mapOfResults.size() == 1
        mapOfResults.get(inputUuid) == loadResult
    }
}