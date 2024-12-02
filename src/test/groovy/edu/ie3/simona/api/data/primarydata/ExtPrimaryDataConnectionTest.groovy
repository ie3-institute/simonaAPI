package edu.ie3.simona.api.data.primarydata

import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtPrimaryDataConnectionTest extends Specification implements DataServiceTestData {

    @Shared
    ActorSystem actorSystem

    @Shared
    Map<String, UUID> extPrimaryDataMapping = Map.of(
            "Pv",
            inputUuid
    )

    def setupSpec() {
        actorSystem = ActorSystem.create()
    }

    def cleanupSpec() {
        TestKit.shutdownActorSystem(actorSystem)
        actorSystem = null
    }

    def "ExtPrimaryDataConnection should provide primary data correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extPrimaryDataConnection = new ExtPrimaryDataConnection(extPrimaryDataMapping)
        extPrimaryDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def primaryData = [:] as HashMap<String, Value>
        def uuid = UUID.randomUUID()
        primaryData.put(uuid.toString(), pValue)

        def convertedPrimaryData = Map.of(uuid, pValue as Value)

        when:
        extPrimaryDataConnection.providePrimaryData(0L, convertedPrimaryData, Optional.of(900L))

        then:
        dataService.expectMsg(new ProvidePrimaryData(0L, convertedPrimaryData, Optional.of(900L)))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtPrimaryDataConnection should convert input data correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extPrimaryDataConnection = new ExtPrimaryDataConnection(extPrimaryDataMapping)
        extPrimaryDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = Map.of("Pv", pValue)

        when:
        extPrimaryDataConnection.convertAndSend(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectMsg(new ProvidePrimaryData(0L, Map.of(inputUuid, pValue), Optional.of(900L)))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtPrimaryDataConnection should send no message, if input data for a not requested asset was provided"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extPrimaryDataConnection = new ExtPrimaryDataConnection(extPrimaryDataMapping)
        extPrimaryDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = Map.of("Load", pValue)

        when:
        extPrimaryDataConnection.convertAndSend(0L, inputDataMap, Optional.empty(), log)

        then:
        dataService.expectNoMessage()
    }
}
