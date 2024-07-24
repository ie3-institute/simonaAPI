package edu.ie3.simona.api.data.results

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.ResultEntity
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities
import edu.ie3.simona.api.data.results.ontology.ResultDataResponseMessageToExt
import edu.ie3.simona.api.exceptions.ConvertionException
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

class ExtResultDataTest extends Specification {

    @Shared
    ActorSystem actorSystem

    @Shared
    UUID loadUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")

    @Shared
    LoadResult loadResult = new LoadResult(
            ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"),
            loadUuid,
            Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN),
            Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
    )

    @Shared
    HashMap<UUID, String> participantResultAssetMapping = Map.of(loadUuid, "Load") as HashMap<UUID, String>

    @Shared
    HashMap<UUID, String> gridResultAssetMapping = [:]

    class WrongResultDataResponseMessageToExt implements ResultDataResponseMessageToExt {}

    def setupSpec() {
        actorSystem = ActorSystem.create()
    }

    def cleanupSpec() {
        TestKit.shutdownActorSystem(actorSystem)
        actorSystem = null
    }

    def "ExtResultsData should request and receive results correctly as ModelResultEntity"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def dataServiceActivation = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extResultData = new ExtResultData(participantResultAssetMapping, gridResultAssetMapping)
        extResultData.setActorRefs(
                dataService.ref(),
                dataServiceActivation.ref(),
                extSimAdapter.ref()
        )

        def sentMsg = new ProvideResultEntities([loadResult])

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultData.queueExtResponseMsg(sentMsg)
        def receivedResults = extResultData.requestResults(0L)

        then:
        dataService.expectMsg(new RequestResultEntities(0L))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataServiceActivation.ref()))
        receivedResults.get("Load") == loadResult
    }

    def "ExtResultsData should fail if wrong response is sent"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def dataServiceActivation = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extResultData = new ExtResultData(participantResultAssetMapping, gridResultAssetMapping)
        extResultData.setActorRefs(
                dataService.ref(),
                dataServiceActivation.ref(),
                extSimAdapter.ref()
        )

        def unexpectedMsg = new WrongResultDataResponseMessageToExt()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultData.queueExtResponseMsg(unexpectedMsg)
        extResultData.requestResults(0L)

        then:
        dataService.expectMsg(new RequestResultEntities(0L))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataServiceActivation.ref()))
        thrown RuntimeException
    }

    def "ExtResultData should convert a list of result entities correctly to a map of resultAssetMappingId to result entity"() {
        given:
            def extResultData = new ExtResultData(participantResultAssetMapping, gridResultAssetMapping)

        when:
            def mapOfResults = extResultData.createResultMap([loadResult])

        then:
            mapOfResults.size() == 1
            mapOfResults.get("Load") == loadResult
    }
}