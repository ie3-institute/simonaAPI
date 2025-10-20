package edu.ie3.simona.api.mapping

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import spock.lang.Shared
import spock.lang.Specification

class ExtEntityMappingTest extends Specification {
    @Shared
    UUID loadUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")

    @Shared
    UUID pvUuid = UUID.fromString("12f5e864-2464-4e43-9a38-5753a439d45f")

    @Shared
    UUID prUuid = UUID.fromString("12f5e864-2464-4e43-9a38-5753a439d46f")

    @Shared
    UUID emUuid = UUID.fromString("60dbc7e4-9718-4bbd-913a-dd26925e68a3")

    @Shared
    ExtEntityEntry extResultEntry = new ExtEntityEntry(
            loadUuid,
            "Load",
            Optional.empty(),
            DataType.RESULT
    )

    @Shared
    ExtEntityEntry extInputEntry = new ExtEntityEntry(
            pvUuid,
            "PV",
            ColumnScheme.parse("p"),
            DataType.PRIMARY
    )

    @Shared
    ExtEntityEntry extPrimaryResultEntry = new ExtEntityEntry(
            prUuid,
            "PR",
            ColumnScheme.parse("p"),
            DataType.PRIMARY_RESULT
    )

    @Shared
    ExtEntityEntry extEmInputEntry = new ExtEntityEntry(
            emUuid,
            "Em",
            Optional.empty(),
            DataType.EM
    )

    def "ExtEntityMapping should return the data types correctly"() {
        when:
        def extEntryMapping = new ExtEntityMapping(assets)
        def types = extEntryMapping.dataTypes

        then:
        types == expectedTypes as Set

        where:
        assets | expectedTypes
        [extResultEntry] | [DataType.RESULT]
        [extInputEntry] | [DataType.PRIMARY]
        [extPrimaryResultEntry] | [DataType.PRIMARY_RESULT]
        [extEmInputEntry] | [DataType.EM]
        [extResultEntry, extInputEntry] | [DataType.RESULT, DataType.PRIMARY]
        [extInputEntry, extEmInputEntry] | [DataType.PRIMARY, DataType.EM]
        [extResultEntry, extInputEntry, extPrimaryResultEntry, extEmInputEntry] | [DataType.RESULT, DataType.PRIMARY, DataType.PRIMARY_RESULT, DataType.EM]
    }

    def "ExtEntityMapping should return all SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extPrimaryResultEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtId2UuidMapping()

        then:
        inputMap.size() == 4
        inputMap.get("Load") == loadUuid
        inputMap.get("PV") == pvUuid
        inputMap.get("PR") == prUuid
        inputMap.get("Em") == emUuid
    }

    def "ExtEntityMapping should return SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extPrimaryResultEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def actual = extEntryMapping.getAssets(DataType.PRIMARY)

        then:
        actual.size() == 1
        actual.getFirst() == pvUuid
    }

    def "ExtEntityMapping should return all external id mapping correctly"() {
        given:
        def extAssetList = List.of(extResultEntry, extInputEntry, extPrimaryResultEntry, extEmInputEntry)
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def inputMap = extEntryMapping.getExtUuid2IdMapping()

        then:
        inputMap.size() == 4
        inputMap.get(loadUuid) == "Load"
        inputMap.get(pvUuid) == "PV"
        inputMap.get(prUuid) == "PR"
        inputMap.get(emUuid) == "Em"
    }
}