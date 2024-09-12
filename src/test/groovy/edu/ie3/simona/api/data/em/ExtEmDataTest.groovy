package edu.ie3.simona.api.data.em

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ExtInputDataPackage
import edu.ie3.simona.api.data.ExtInputDataValue
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.exceptions.ConvertionException
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class ExtEmDataTest extends Specification {

    @Shared
    ActorSystem actorSystem

    @Shared
    UUID inputUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")

    @Shared
    PValue pValue = new PValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN))

    @Shared
    Map<String, UUID> extEmDataMapping = Map.of(
            "Em",
            inputUuid
    )

    class TestInputDataValue implements ExtInputDataValue {
        private PValue value

        TestInputDataValue(PValue value) {
            this.value = value
        }

        PValue getValue() {
            return value
        }
    }

    class TestEmDataFactory implements EmDataFactory {
        @Override
        PValue convert(ExtInputDataValue entity) throws ConvertionException {
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

    def "ExtEmData should provide em data correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extEmData = new ExtEmData(new TestEmDataFactory(), extEmDataMapping)
        extEmData.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def emData = new HashMap<String, Value>()
        def uuid = UUID.randomUUID()
        emData.put(uuid.toString(), pValue)

        def convertedEmData = Map.of(uuid, pValue)

        when:
        extEmData.provideEmData(0L, convertedEmData, Optional.of(900L))

        then:
        dataService.expectMsg(new ProvideEmSetPointData(0, convertedEmData, Optional.of(900L)))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEmData should convert ExtInputDataPackage to a map"() {
        given:
        def extEmData = new ExtEmData(new TestEmDataFactory(), extEmDataMapping)
        def inputDataMap = Map.of("Em", new TestInputDataValue(pValue))
        def inputDataPackage = new ExtInputDataPackage(inputDataMap, Optional.of(900L))

        when:
        def emDataMap = extEmData.createExtEmDataMap(inputDataPackage)

        then:
        emDataMap.get(inputUuid) == pValue
    }

    def "ExtEmData should throw an exception, if input data for a not requested asset was provided"() {
        given:
        def extEmData = new ExtEmData(new TestEmDataFactory(), extEmDataMapping)
        def inputDataMap = Map.of("Load", new TestInputDataValue(pValue))
        def inputDataPackage = new ExtInputDataPackage(inputDataMap, Optional.of(900L))

        when:
        extEmData.createExtEmDataMap(inputDataPackage)

        then:
        thrown IllegalArgumentException
    }

}
