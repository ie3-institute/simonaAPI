package edu.ie3.simona.api.simulation.mapping

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.simona.api.data.mapping.DataType
import edu.ie3.simona.api.data.mapping.ExtEntityEntry
import edu.ie3.simona.api.data.mapping.ExtEntityMapping
import spock.lang.Shared
import spock.lang.Specification

class ExtEntityMappingTest extends Specification {
    @Shared
    UUID loadUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")

    @Shared
    ExtEntityEntry extResultEntry = new ExtEntityEntry(
            loadUuid,
            "Load",
            ColumnScheme.parse("p"),
            DataType.EXT_PARTICIPANT_RESULT
    )

    @Shared
    ExtEntityEntry extInputEntry = new ExtEntityEntry(
            loadUuid,
            "Load",
            ColumnScheme.parse("p"),
            DataType.EXT_PRIMARY_INPUT
    )

    def "ExtEntityMapping should return SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtId2UuidMapping(DataType.EXT_PRIMARY_INPUT)

        then:
        inputMap.size() == 1
        inputMap.get("Load") == loadUuid
    }

    def "ExtEntityMapping should return external id mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtUuid2IdMapping(DataType.EXT_PRIMARY_INPUT)

        then:
        inputMap.size() == 1
        inputMap.get(loadUuid) == "Load"
    }
}