package edu.ie3.simona.api.data.em


import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ExtInputDataContainer
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtEmDataTest extends Specification implements DataServiceTestData {

    @Shared
    ActorSystem actorSystem

    @Shared
    Map<String, UUID> extEmDataMapping = Map.of(
            "Em",
            inputUuid
    )

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
        def extEmDataConnection = new ExtEmDataConnection(extEmDataMapping)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def emData = [:] as HashMap<String, Value>
        def uuid = UUID.randomUUID()
        emData.put(uuid.toString(), pValue)

        def convertedEmData = Map.of(uuid, pValue as PValue)

        when:
        extEmDataConnection.provideEmData(0L, convertedEmData, Optional.of(900L))

        then:
        dataService.expectMsg(new ProvideEmSetPointData(0, convertedEmData, Optional.of(900L)))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEmData should convert ExtInputDataPackage to a map"() {
        given:
        def extEmData = new ExtEmDataConnection(extEmDataMapping)
        def inputDataMap = Map.of("Em", pValue)
        def inputDataContainer = new ExtInputDataContainer(0L, inputDataMap, 900L)

        when:
        def emDataMap = extEmData.convertExternalInputToEmSetPoints(inputDataContainer)

        then:
        emDataMap.get(inputUuid) == pValue
    }

    def "ExtEmData should throw an exception, if input data for a not requested asset was provided"() {
        given:
        def extEmData = new ExtEmDataConnection(extEmDataMapping)
        def inputDataMap = Map.of("Load", pValue)
        def inputDataContainer = new ExtInputDataContainer(0L, inputDataMap, 900L)

        when:
        extEmData.convertExternalInputToEmSetPoints(inputDataContainer)

        then:
        thrown IllegalArgumentException
    }

}
