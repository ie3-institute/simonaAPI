package edu.ie3.simona.api.mapping

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.datamodel.models.input.AssetInput
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SubGridContainer
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.SystemParticipantInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import static edu.ie3.simona.api.mapping.DataType.*

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
    ExtEntityEntry extResultEntry = new ExtEntityEntry(loadUuid, "Load", Optional.empty(), RESULT)

    @Shared
    ExtEntityEntry extInputEntry = new ExtEntityEntry(pvUuid, "PV", ColumnScheme.parse("p"), PRIMARY)

    @Shared
    ExtEntityEntry extPrimaryResultEntry = new ExtEntityEntry(prUuid, "PR", ColumnScheme.parse("p"), PRIMARY_RESULT)

    @Shared
    ExtEntityEntry extEmInputEntry = new ExtEntityEntry(emUuid, "Em", Optional.empty(), EM)


    def "ExtEntityMapping can be created from a grid container correctly"() {
        given:
        def node = new NodeInput(UUID.randomUUID(), "node", Quantities.getQuantity(1d, PowerSystemUnits.PU), false, NodeInput.DEFAULT_GEO_POSITION, GermanVoltageLevelUtils.LV, 1)
        def em = new EmInput(UUID.randomUUID(), "em", "", null)
        def participant = new FixedFeedInInput(UUID.randomUUID(), "ffi", node, null, em, Quantities.getQuantity(10, PowerSystemUnits.KILOVOLTAMPERE), 0.9)

        List<AssetInput> gridAssets = [node]
        List<SystemParticipantInput> participantInputs = [participant]

        def grid = new SubGridContainer(
                "test grid",
                1,
                new RawGridElements(gridAssets),
                new SystemParticipants(participantInputs),
                new GraphicElements([])
        )

        when:
        def mapping = new ExtEntityMapping(grid)

        then:
        mapping.extId2UuidMapping == [
                "node": node.uuid,
                "em": em.uuid,
                "ffi": participant.uuid
        ]

        mapping.extUuid2IdMapping == [
                (node.uuid): "node",
                (em.uuid): "em",
                (participant.uuid): "ffi"
        ]

        mapping.gridAssets == [node.uuid] as Set
        mapping.participants == [participant.uuid] as Set
        mapping.ems == [em.uuid] as Set
    }

    def "ExtEntityMapping can include specific external ids correctly"() {
        given:
        def node = new NodeInput(UUID.randomUUID(), "node", Quantities.getQuantity(1d, PowerSystemUnits.PU), false, NodeInput.DEFAULT_GEO_POSITION, GermanVoltageLevelUtils.LV, 1)
        def em = new EmInput(UUID.randomUUID(), "em", "", null)
        def participant = new FixedFeedInInput(UUID.randomUUID(), "ffi", node, null, em, Quantities.getQuantity(10, PowerSystemUnits.KILOVOLTAMPERE), 0.9)

        List<AssetInput> gridAssets = [node]
        List<SystemParticipantInput> participantInputs = [participant]

        def grid = new SubGridContainer(
                "test grid",
                1,
                new RawGridElements(gridAssets),
                new SystemParticipants(participantInputs),
                new GraphicElements([])
        )
        def mapping = new ExtEntityMapping(grid)

        when:
        def updated = mapping.include(RESULT, ["ffi"], Optional.empty())

        then:
        // only the included ids will be returned, other grid ids will be ignored
        mapping.getAssets(RESULT) == [node.uuid, participant.uuid, em.uuid]
        updated.getAssets(RESULT) == [participant.uuid]
    }

    def "ExtEntityMapping should return the data types correctly"() {
        when:
        def extEntryMapping = new ExtEntityMapping(assets)
        def types = extEntryMapping.dataTypes

        then:
        types == expectedTypes as Set

        where:
        assets | expectedTypes
        [extResultEntry] | [RESULT]
        [extInputEntry] | [PRIMARY]
        [extPrimaryResultEntry] | [PRIMARY_RESULT]
        [extEmInputEntry] | [EM]
        [extResultEntry, extInputEntry] | [RESULT, PRIMARY]
        [extInputEntry, extEmInputEntry] | [PRIMARY, EM]
        [extResultEntry, extInputEntry, extPrimaryResultEntry, extEmInputEntry] | [RESULT, PRIMARY, PRIMARY_RESULT, EM]
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

    def "ExtEntityMapping should return SIMONA uuid mapping correctly"() {
        given:
        def extAssetList = [extResultEntry, extInputEntry, extPrimaryResultEntry, extEmInputEntry]
        def extEntryMapping = new ExtEntityMapping(extAssetList)

        when:
        def actual = extEntryMapping.getAssets(PRIMARY)

        then:
        actual.size() == 1
        actual.getFirst() == pvUuid
    }

    def "ExtEntityMapping should return assets uuids correctly"() {
        given:
        def node = new NodeInput(UUID.fromString("4476402e-d0e7-4975-935b-6861a00a2a5e"), "node", Quantities.getQuantity(1d, PowerSystemUnits.PU), false, NodeInput.DEFAULT_GEO_POSITION, GermanVoltageLevelUtils.LV, 1)
        def em = new EmInput(UUID.fromString("d7c054b2-f5a8-4f88-99e1-59c734fc4655"), "em", "", null)
        def participant = new FixedFeedInInput(UUID.fromString("4392e420-dc91-4867-a85f-b05d53896dd2"), "ffi", node, null, em, Quantities.getQuantity(10, PowerSystemUnits.KILOVOLTAMPERE), 0.9)

        List<AssetInput> gridAssets = [node]
        List<SystemParticipantInput> participantInputs = [participant]

        def grid = new SubGridContainer(
                "test grid",
                1,
                new RawGridElements(gridAssets),
                new SystemParticipants(participantInputs),
                new GraphicElements([])
        )

        when:
        def mapping = new ExtEntityMapping(grid).include(includedType, includedExtAssets, Optional.empty())
        def actual = mapping.getAssets(requestedType)

        then:
        actual.size() == expectedSize
        actual == expectedUuids

        where:
        includedType   | includedExtAssets  | requestedType  | expectedSize | expectedUuids
        PRIMARY        | []                 | PRIMARY        | 1            | [UUID.fromString("4392e420-dc91-4867-a85f-b05d53896dd2")]
        PRIMARY_RESULT | ["ffi"]            | RESULT         | 1            | [UUID.fromString("4392e420-dc91-4867-a85f-b05d53896dd2")]
        RESULT         | []                 | RESULT         | 3            | [UUID.fromString("4476402e-d0e7-4975-935b-6861a00a2a5e"), UUID.fromString("4392e420-dc91-4867-a85f-b05d53896dd2"), UUID.fromString("d7c054b2-f5a8-4f88-99e1-59c734fc4655")]
        RESULT         | []                 | PRIMARY_RESULT | 1            | [UUID.fromString("4392e420-dc91-4867-a85f-b05d53896dd2")]
        RESULT         | ["ffi", "node"]    | RESULT         | 2            | [UUID.fromString("4476402e-d0e7-4975-935b-6861a00a2a5e"), UUID.fromString("4392e420-dc91-4867-a85f-b05d53896dd2")]

    }
}