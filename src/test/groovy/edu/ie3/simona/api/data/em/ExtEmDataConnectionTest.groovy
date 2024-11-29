package edu.ie3.simona.api.data.em

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class ExtEmDataConnectionTest extends Specification implements DataServiceTestData {

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

    def "ExtEmDataConnection should provide em data correctly"() {
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

    def "ExtEmDataConnection should convert input data correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extEmDataConnection = new ExtEmDataConnection(extEmDataMapping)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = Map.of("Em", pValue)

        when:
        extEmDataConnection.convertAndSend(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectMsg(new ProvideEmSetPointData(0L, Map.of(inputUuid, pValue), Optional.of(900L)))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEmDataConnection should send no message, if input data for a not requested asset was provided"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extEmDataConnection = new ExtEmDataConnection(extEmDataMapping)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = Map.of("Load", pValue)

        when:
        extEmDataConnection.convertAndSend(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectNoMessage()
    }

}
