package edu.ie3.simona.api.data.primarydata

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class ExtPrimaryDataTest extends Specification {

    @Shared
    ActorSystem actorSystem

    def setupSpec() {
        actorSystem = ActorSystem.create()
    }

    def cleanupSpec() {
        TestKit.shutdownActorSystem(actorSystem)
        actorSystem = null
    }

    def "ExtPrimaryData should provide primary data correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extPrimaryData = new ExtPrimaryData(dataService.ref(), extSimAdapter.ref())

        def primaryData = new HashMap<UUID, Value>()
        primaryData.put(UUID.randomUUID(), new PValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN)))

        when:
        extPrimaryData.providePrimaryData(0, primaryData)

        then:
        dataService.expectMsg(new ProvidePrimaryData(0, primaryData))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }
}
