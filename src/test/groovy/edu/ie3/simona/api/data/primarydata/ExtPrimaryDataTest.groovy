package edu.ie3.simona.api.data.primarydata

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.ResultEntity
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData
import edu.ie3.simona.api.data.results.ResultDataFactory
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class ExtPrimaryDataTest extends Specification {

    @Shared
    ActorSystem actorSystem


    class DefaultPrimaryDataFactory implements PrimaryDataFactory {

        @Override
        Value convert(Object entity) throws Exception {
            if (entity.getClass() == Value.class) {
                return (Value) entity
            } else {
                return null
            }
        }
    }

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
        def extPrimaryData = new ExtPrimaryData(dataService.ref(), extSimAdapter.ref(), new DefaultPrimaryDataFactory())

        def primaryData = new HashMap<String, Object>()
        def uuid = UUID.randomUUID()
        primaryData.put(uuid.toString(), new PValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN)))

        def convertedPrimaryData = new HashMap<UUID, Value>()
        convertedPrimaryData.put(uuid, new PValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN)))

        when:
        extPrimaryData.providePrimaryData(0, primaryData)

        then:
        dataService.expectMsg(new ProvidePrimaryData(0, convertedPrimaryData))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }
}
