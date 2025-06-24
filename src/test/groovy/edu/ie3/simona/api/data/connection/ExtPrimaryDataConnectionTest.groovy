package edu.ie3.simona.api.data.connection

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.ontology.DataMessageFromExt
import edu.ie3.simona.api.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.ontology.primary.ProvidePrimaryData
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtPrimaryDataConnectionTest extends Specification implements DataServiceTestData {

    @Shared
    ActorTestKit testKit

    @Shared
    Map<UUID, Class<Value>> assetToValueClasses = [ (inputUuid): PValue] as Map

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
        def extPrimaryDataConnection = new ExtPrimaryDataConnection(assetToValueClasses)
        extPrimaryDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def primaryData = Map.of(inputUuid, pValue as Value)

        when:
        extPrimaryDataConnection.sendPrimaryData(0L, primaryData, Optional.of(900L), log)

        then:
        dataService.expectMessage(new ProvidePrimaryData(0L, primaryData, Optional.of(900L)))
        extSimAdapter.expectMessage(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtPrimaryDataConnection should send no message, if input data is empty"() {
        given:
        def dataService = testKit.createTestProbe(DataMessageFromExt)
        def extSimAdapter = testKit.createTestProbe(ScheduleDataServiceMessage)
        def extPrimaryDataConnection = new ExtPrimaryDataConnection(assetToValueClasses)
        extPrimaryDataConnection.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )
        def inputDataMap = [:]

        when:
        extPrimaryDataConnection.sendPrimaryData(0L, inputDataMap, Optional.empty(), log)

        then:
        dataService.expectNoMessage()
    }
}
