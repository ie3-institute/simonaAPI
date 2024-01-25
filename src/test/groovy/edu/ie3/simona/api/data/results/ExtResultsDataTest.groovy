package edu.ie3.simona.api.data.results

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.ResultEntity
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.simona.api.data.ev.ExtEvData
import edu.ie3.simona.api.data.ev.model.EvModel
import edu.ie3.simona.api.data.ev.ontology.*
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.results.ontology.ProvideResultEntities
import edu.ie3.simona.api.data.results.ontology.RequestResultEntities
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Power
import java.time.ZonedDateTime

class ExtResultsDataTest extends Specification {

    @Shared
    ActorSystem actorSystem
    @Shared
    UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
    @Shared
    UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
    @Shared
    Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
    @Shared
    Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)

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
        def extResultsData = new ExtResultsData(dataService.ref(), extSimAdapter.ref())

        def loadResult = new LoadResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)

        def listOfResults = new ArrayList()
        listOfResults.add(loadResult)

        def sentMsg = new ProvideResultEntities(listOfResults)

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultsData.queueExtResponseMsg(sentMsg)
        def receivedResults = extResultsData.requestResultObjects()

        then:
        dataService.expectMsg(new RequestResultEntities())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        receivedResults == extResultsData.convertResultsList(sentMsg.results())
    }

    def "ExtResultsData should request and receive results correctly as a list of results entities"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extResultsData = new ExtResultsData(dataService.ref(), extSimAdapter.ref())

        def loadResult = new LoadResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)

        def listOfResults = new ArrayList()
        listOfResults.add(loadResult)

        def sentMsg = new ProvideResultEntities(listOfResults)

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultsData.queueExtResponseMsg(sentMsg)
        def receivedResults = extResultsData.requestResults()

        then:
        dataService.expectMsg(new RequestResultEntities())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        receivedResults == sentMsg.results()
    }

    def "ExtResultsData should fail if wrong response is sent"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extResultsData = new ExtResultsData(dataService.ref(), extSimAdapter.ref())

        def unexpectedMsg = new ProvideCurrentPrices()

        when:
        // we need to queue the msg beforehand because the receive method is blocking
        extResultsData.queueExtResponseMsg(unexpectedMsg)
        extResultsData.requestResults()

        then:
        dataService.expectMsg(new RequestResultEntities())
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
        thrown RuntimeException
    }
}
