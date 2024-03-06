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
            loadUuid,
            ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"),
            UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52"),
            Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN),
            Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
    )

    class DefaultResultFactory implements ResultDataFactory {

        @Override
        Object convert(ResultEntity entity) throws ConvertionException {
            if (entity instanceof LoadResult) {
                return "{\"p\":\"" + entity.p.toString() + ",\"q\":\"" + entity.q.toString() + "\"}"
            } else {
                throw new ConvertionException("This factory can convert LoadResult's only!")
            }
        }
    }

    class WrongResultDataResponseMessageToExt implements ResultDataResponseMessageToExt {}

    def setupSpec() {
        actorSystem = ActorSystem.create()
    }

    def cleanupSpec() {
        TestKit.shutdownActorSystem(actorSystem)
        actorSystem = null
    }

    def "ExtResultsData should request and receive results correctly as Object"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def resultDataFactory = new DefaultResultFactory()
        def extResultData = new ExtResultData(dataService.ref(), extSimAdapter.ref(), resultDataFactory)

        def sentMsg = new ProvideResultEntities([loadResult])

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultData.queueExtResponseMsg(sentMsg)
        def receivedResults = extResultData.requestResultObjects()

        then:
        dataService.expectMsg(new RequestResultEntities())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        receivedResults.get(loadUuid.toString()) == resultDataFactory.convert(loadResult)
    }

    def "ExtResultsData should request and receive results correctly as a list of results entities"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extResultData = new ExtResultData(dataService.ref(), extSimAdapter.ref(), new DefaultResultFactory())

        def sentMsg = new ProvideResultEntities([loadResult])

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultData.queueExtResponseMsg(sentMsg)
        def receivedResults = extResultData.requestResults()

        then:
        dataService.expectMsg(new RequestResultEntities())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        receivedResults == sentMsg.results()
    }

    def "ExtResultsData should fail if wrong response is sent"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extResultData = new ExtResultData(dataService.ref(), extSimAdapter.ref(), new DefaultResultFactory())

        def unexpectedMsg = new WrongResultDataResponseMessageToExt()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultData.queueExtResponseMsg(unexpectedMsg)
        extResultData.requestResults()

        then:
        dataService.expectMsg(new RequestResultEntities())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        thrown RuntimeException
    }

    def "ExtResultData should convert a list of result entities correctly to a map of objects"() {
        given:
            def dataService = new TestProbe(actorSystem)
            def extSimAdapter = new TestProbe(actorSystem)
            def extResultData = new ExtResultData(dataService.ref(), extSimAdapter.ref(), new DefaultResultFactory())

        when:
            def mapOfResults = extResultData.convertResultsList([loadResult])

        then:
            mapOfResults.size() == 1
            mapOfResults.get(loadUuid.toString()) == "{\"p\":\"10 kW,\"q\":\"10 kvar\"}"
    }
}
