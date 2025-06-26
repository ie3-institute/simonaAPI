package edu.ie3.simona.api.mapping

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.simona.api.simulation.mapping.ExtEntityEntry
import spock.lang.Shared
import spock.lang.Specification

class ExtEntityMappingTest extends Specification {
    @Shared
    UUID loadUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")

    @Shared
    UUID pvUuid = UUID.fromString("12f5e864-2464-4e43-9a38-5753a439d45f")

    @Shared
    UUID emUuid = UUID.fromString("60dbc7e4-9718-4bbd-913a-dd26925e68a3")

    @Shared
    ExtEntityEntry extResultEntry = new ExtEntityEntry(
            loadUuid,
            "Load",
            Optional.empty(),
            DataType.EXT_PARTICIPANT_RESULT
    )

    @Shared
    ExtEntityEntry extInputEntry = new ExtEntityEntry(
            pvUuid,
            "PV",
            ColumnScheme.parse("p"),
            DataType.EXT_PRIMARY_INPUT
    )

    @Shared
    ExtEntityEntry extEmInputEntry = new ExtEntityEntry(
            emUuid,
            "Em",
            Optional.empty(),
            DataType.EXT_EM_INPUT
    )

    def "ExtEntityMapping should return the data types correctly"() {
        when:
        def extEntryMapping = new ExtEntityMapping(assets)
        def types = extEntryMapping.dataTypes

        then:
        types == expectedTypes as Set

        where:
        assets | expectedTypes
        [extResultEntry] | [DataType.EXT_PARTICIPANT_RESULT]
        [extInputEntry] | [DataType.EXT_PRIMARY_INPUT]
        [extEmInputEntry] | [DataType.EXT_EM_INPUT]
        [extResultEntry, extInputEntry] | [DataType.EXT_PARTICIPANT_RESULT, DataType.EXT_PRIMARY_INPUT]
        [extInputEntry, extEmInputEntry] | [DataType.EXT_PRIMARY_INPUT, DataType.EXT_EM_INPUT]
        [extResultEntry, extInputEntry, extEmInputEntry] | [DataType.EXT_PARTICIPANT_RESULT, DataType.EXT_PRIMARY_INPUT, DataType.EXT_EM_INPUT]
    }

    def "ExtEntityMapping should return the entries correctly"() {
        when:
        def extEntryMapping = new ExtEntityMapping(assets)
        def types = extEntryMapping.getEntries(dataType)

        then:
        types == expectedEntries

        where:
        assets | dataType | expectedEntries
        [extResultEntry, extInputEntry, extEmInputEntry] | DataType.EXT_PARTICIPANT_RESULT | [extResultEntry]
        [extResultEntry, extInputEntry, extEmInputEntry] | DataType.EXT_PRIMARY_INPUT | [extInputEntry]
        [extResultEntry, extInputEntry, extEmInputEntry] | DataType.EXT_EM_INPUT | [extEmInputEntry]
    }

    def "ExtEntityMapping should return all SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtId2UuidMapping()

        then:
        inputMap.size() == 3
        inputMap.get("Load") == loadUuid
        inputMap.get("PV") == pvUuid
        inputMap.get("Em") == emUuid
    }

    def "ExtEntityMapping should return SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtId2UuidMapping(DataType.EXT_PRIMARY_INPUT)

        then:
        inputMap.size() == 1
        inputMap.get("PV") == pvUuid
    }

    def "ExtEntityMapping should return multiple SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtId2UuidMapping(DataType.EXT_PRIMARY_INPUT, DataType.EXT_EM_INPUT)

        then:
        inputMap.size() == 2
        inputMap.get("PV") == pvUuid
        inputMap.get("Em") == emUuid
    }

    def "ExtEntityMapping should return all external id mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtUuid2IdMapping()

        then:
        inputMap.size() == 3
        inputMap.get(loadUuid) == "Load"
        inputMap.get(pvUuid) == "PV"
        inputMap.get(emUuid) == "Em"
    }

    def "ExtEntityMapping should return external id mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtUuid2IdMapping(DataType.EXT_PRIMARY_INPUT)

        then:
        inputMap.size() == 1
        inputMap.get(pvUuid) == "PV"
    }

    def "ExtEntityMapping should return multiple external id mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtUuid2IdMapping(DataType.EXT_PRIMARY_INPUT, DataType.EXT_EM_INPUT)

        then:
        inputMap.size() == 2
        inputMap.get(pvUuid) == "PV"
        inputMap.get(emUuid) == "Em"
    }
}