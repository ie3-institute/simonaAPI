package edu.ie3.simona.api.data.em

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.em.ontology.ProvideEmSetPointData
import edu.ie3.simona.api.data.ontology.DataMessageFromExt
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtEmDataConnectionTest extends Specification implements DataServiceTestData {

    @Shared
    ActorTestKit testKit

    @Shared
    Map<String, UUID> extEmDataMapping = Map.of(
            "Em",
            inputUuid
    )

    def setupSpec() {
        testKit = ActorTestKit.create()
    }

    def cleanupSpec() {
        testKit.shutdownTestKit()
        testKit = null
    }

    def "ExtEmDataConnection should provide em data correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
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
        dataService.expectMessage(new ProvideEmSetPointData(0, convertedEmData, Optional.of(900L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEmDataConnection should convert input data correctly"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extEmDataConnection = new ExtEmDataConnection(extEmDataMapping)
        extEmDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = Map.of("Em", pValue)

        when:
        extEmDataConnection.convertAndSend(0L, inputDataMap, Optional.of(900L), log)

        then:
        dataService.expectMessage(new ProvideEmSetPointData(0L, Map.of(inputUuid, pValue), Optional.of(900L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtEmDataConnection should send no message, if input data for a not requested asset was provided"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
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
