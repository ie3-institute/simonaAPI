package edu.ie3.simona.api.data.results

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.simona.api.data.ontology.DataMessageFromExt
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities
import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt
import edu.ie3.simona.api.simulation.ExtSimulation
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
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
    Map<UUID, String> participantResultAssetMapping = Map.of(inputUuid, "Load")

    @Shared
    Map<UUID, String> gridResultAssetMapping = [:]

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
        def dataServiceActivation = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extResultDataConnection = new ExtResultDataConnection(participantResultAssetMapping, gridResultAssetMapping)
        extResultDataConnection.setActorRefs(
                dataService.ref(),
                dataServiceActivation.ref(),
                extSimAdapter.ref()
        )

        def sentMsg = new ProvideResultEntities([loadResult])

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultDataConnection.queueExtResponseMsg(sentMsg)
        def receivedResults = extResultDataConnection.requestResults(0L)

        then:
        dataService.expectMessage(new RequestResultEntities(0L))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataServiceActivation.ref()))
        receivedResults.get("Load") == loadResult
    }

    def "ExtResultsData should fail if wrong response is sent"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def dataServiceActivation = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extResultDataConnection = new ExtResultDataConnection(participantResultAssetMapping, gridResultAssetMapping)
        extResultDataConnection.setActorRefs(
                dataService.ref(),
                dataServiceActivation.ref(),
                extSimAdapter.ref()
        )

        def unexpectedMsg = new WrongResultDataResponseMessageToExt()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultDataConnection.queueExtResponseMsg(unexpectedMsg)
        extResultDataConnection.requestResults(0L)

        then:
        dataService.expectMessage(new RequestResultEntities(0L))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataServiceActivation.ref()))
        thrown RuntimeException
    }

    def "ExtResultData should convert a list of result entities correctly to a map of resultAssetMappingId to result entity"() {
        given:
        def extResultDataConnection = new ExtResultDataConnection(participantResultAssetMapping, gridResultAssetMapping)

        when:
        def mapOfResults = extResultDataConnection.createResultMap([loadResult])

        then:
        mapOfResults.size() == 1
        mapOfResults.get("Load") == loadResult
    }

    def "ExtResultData should throw an exception, if a result with a wrong data type was provided"() {
        given:
        def extResultDataConnection = new ExtResultDataConnection(participantResultAssetMapping, gridResultAssetMapping)
        Quantity<ElectricCurrent> iAMag = Quantities.getQuantity(100, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
        Quantity<Angle> iAAng = Quantities.getQuantity(45, StandardUnits.ELECTRIC_CURRENT_ANGLE)
        Quantity<ElectricCurrent> iBMag = Quantities.getQuantity(150, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
        Quantity<Angle> iBAng = Quantities.getQuantity(30, StandardUnits.ELECTRIC_CURRENT_ANGLE)
        def wrongResult = new LineResult(
                ZonedDateTime.parse("2020-01-30T17:26:44Z"), inputUuid, iAMag, iAAng, iBMag, iBAng
        )

        when:
        extResultDataConnection.createResultMap([wrongResult])

        then:
        thrown IllegalArgumentException
    }
}