package edu.ie3.simona.api.data.primarydata

import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ontology.DataMessageFromExt
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtPrimaryDataConnectionTest extends Specification implements DataServiceTestData {

    @Shared
    ActorTestKit testKit

    @Shared
    Map<String, UUID> extPrimaryDataMapping = Map.of(
            "Pv",
            inputUuid
    )

    def setupSpec() {
        testKit = ActorTestKit.create()
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

    def "ExtPrimaryDataConnection should provide primary data correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
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
        dataService.expectMessage(new ProvidePrimaryData(0L, convertedPrimaryData, Optional.of(900L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtPrimaryDataConnection should convert input data correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extPrimaryDataConnection = new ExtPrimaryDataConnection(extPrimaryDataMapping)
        extPrimaryDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = Map.of("Pv", pValue)

        when:
        extPrimaryDataConnection.convertAndSend(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectMessage(new ProvidePrimaryData(0L, Map.of(inputUuid, pValue), Optional.of(900L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtPrimaryDataConnection should send no message, if input data for a not requested asset was provided"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
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
