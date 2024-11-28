package edu.ie3.simona.api.data.primarydata

import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ExtInputDataContainer
import edu.ie3.simona.api.data.ontology.ScheduleDataServiceMessage
import edu.ie3.simona.api.data.primarydata.ontology.ProvidePrimaryData
import edu.ie3.simona.api.test.common.DataServiceTestData
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestProbe
import org.apache.pekko.testkit.javadsl.TestKit
import spock.lang.Shared
import spock.lang.Specification

class ExtPrimaryDataTest extends Specification implements DataServiceTestData {

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

    def "ExtPrimaryData should provide primary data correctly"() {
        given:
        def dataService = new TestProbe(actorSystem)
        def extSimAdapter = new TestProbe(actorSystem)
        def extPrimaryData = new ExtPrimaryData(extPrimaryDataMapping)
        extPrimaryData.setActorRefs(
                dataService.ref(),
                extSimAdapter.ref()
        )

        def primaryData = [:] as HashMap<String, Value>
        def uuid = UUID.randomUUID()
        primaryData.put(uuid.toString(), pValue)

        def convertedPrimaryData = Map.of(uuid, pValue as Value)

        when:
        extPrimaryData.providePrimaryData(0L, convertedPrimaryData, Optional.of(900L))

        then:
        dataService.expectMsg(new ProvidePrimaryData(0L, convertedPrimaryData, Optional.of(900L)))
        extSimAdapter.expectMsg(new ScheduleDataServiceMessage(dataService.ref()))
    }

    def "ExtPrimaryData should convert ExtInputDataPackage to a map"() {
        given:
            def extPrimaryData = new ExtPrimaryData(extPrimaryDataMapping)
            def inputDataMap = Map.of("Pv", pValue)
            def inputDataContainer = new ExtInputDataContainer(0L, inputDataMap, 900L)

        when:
            def primaryDataMap = extPrimaryData.convertExternalInputToPrimaryData(inputDataContainer)

        then:
            primaryDataMap.get(inputUuid) == pValue
    }

    def "ExtPrimaryData should throw an exception, if input data for a not requested asset was provided"() {
        given:
        def extPrimaryData = new ExtPrimaryData(extPrimaryDataMapping)
        def inputDataMap = Map.of("Load", pValue)
        def inputDataContainer = new ExtInputDataContainer(0L, inputDataMap, 900L)

        when:
            extPrimaryData.convertExternalInputToPrimaryData(inputDataContainer)

        then:
            thrown IllegalArgumentException
    }
}
