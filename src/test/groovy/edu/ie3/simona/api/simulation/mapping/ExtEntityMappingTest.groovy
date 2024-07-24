package edu.ie3.simona.api.simulation.mapping

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import spock.lang.Shared
import spock.lang.Specification

class ExtEntityMappingTest extends Specification {
    @Shared
    UUID loadUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")

    @Shared
    ExtEntityEntry extResultEntry = new ExtEntityEntry(
            loadUuid,
            "Load",
            ColumnScheme.parse("p").get(),
            "result_participant"
    )

    @Shared
    ExtEntityEntry extInputEntry = new ExtEntityEntry(
            loadUuid,
            "Load",
            ColumnScheme.parse("p").get(),
            "input"
    )

    def "ExtEntityMapping should return external id to SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtIdUuidMapping("input")

        then:
        inputMap.size() == 1
        inputMap.get("Load") == loadUuid
    }


    def "ExtEntityMapping should return external id to SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtUuidIdMapping("input")

        then:
        inputMap.size() == 1
        inputMap.get(loadUuid) == "Load"
    }
}