package edu.ie3.simona.api.data.primarydata

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ExtInputDataPackage
import edu.ie3.simona.api.data.ExtInputDataValue
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData
import edu.ie3.simona.api.exceptions.ConvertionException
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class ExtPrimaryDataTest extends Specification {

    @Shared
    ActorSystem actorSystem

    @Shared
    UUID inputUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")

    @Shared
    PValue pValue = new PValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN))

    @Shared
    Map<String, UUID> extPrimaryDataMapping = Map.of(
            "Pv",
            inputUuid
    )

    class TestInputDataValue implements ExtInputDataValue {
        private Value value

        TestInputDataValue(Value value) {
            this.value = value
        }

        Value getValue() {
            return value
        }
    }

    class TestPrimaryDataFactory implements PrimaryDataFactory {
        @Override
        Value convert(ExtInputDataValue entity) throws ConvertionException {
            if (entity instanceof TestInputDataValue) {
                return entity.getValue()
            } else {
                throw new ConvertionException("This factory can only convert PValue entities.")
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
        def extPrimaryData = new ExtPrimaryData(new TestPrimaryDataFactory(), extPrimaryDataMapping)
        extPrimaryData.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def primaryData = new HashMap<String, Value>()
        def uuid = UUID.randomUUID()
        primaryData.put(uuid.toString(), pValue)

        def convertedPrimaryData = Map.of(uuid, pValue)

        when:
        extPrimaryData.providePrimaryData(0L, convertedPrimaryData, Optional.of(900L))

        then:
        dataService.expectMsg(new ProvidePrimaryData(0L, convertedPrimaryData, Optional.of(900L)))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtPrimaryData should convert ExtInputDataPackage to a map"() {
        given:
            def extPrimaryData = new ExtPrimaryData(new TestPrimaryDataFactory(), extPrimaryDataMapping)
            def inputDataMap = Map.of("Pv", new TestInputDataValue(pValue))
            def inputDataPackage = new ExtInputDataPackage(inputDataMap, Optional.of(900L))

        when:
            def primaryDataMap = extPrimaryData.createExtPrimaryDataMap(inputDataPackage)

        then:
            primaryDataMap.get(inputUuid) == pValue
    }

    def "ExtPrimaryData should throw an exception, if input data for a not requested asset was provided"() {
        given:
        def extPrimaryData = new ExtPrimaryData(new TestPrimaryDataFactory(), extPrimaryDataMapping)
        def inputDataMap = Map.of("Load", new TestInputDataValue(pValue))
        def inputDataPackage = new ExtInputDataPackage(inputDataMap, Optional.of(900L))

        when:
            extPrimaryData.createExtPrimaryDataMap(inputDataPackage)

        then:
            thrown IllegalArgumentException
    }
}
