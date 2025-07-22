package edu.ie3.simona.api.simulation.mapping

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.simona.api.mapping.DataType
import edu.ie3.simona.api.mapping.ExtEntityEntry
import spock.lang.Specification

import java.nio.file.Path

class ExtEntityMappingSourceTest extends Specification {

    def "An ExtEntityMappingSource can create a naming correctly"() {
        given:
        def naming = new ExtEntityMappingSource.ExtEntityNaming("ext_entity_mapping")

        when:
        def ext_entity_naming = naming.getEntityName(ExtEntityEntry)
        def other = naming.getEntityName(NodeInput)

        then:
        ext_entity_naming == Optional.of("ext_entity_mapping")
        other == Optional.of("node_input")
    }

    def "An ExtEntityMappingSource can read a mapping from file correctly"() {
        given:
        Path filePath = Path.of(ExtEntityMappingSourceTest.getResource("ext_entity_mapping.csv").toURI())

        when:
        def actual = ExtEntityMappingSource.fromFile(filePath)

        then:
        actual.getExtUuid2IdMapping(DataType.EXT_PRIMARY_INPUT).size() == 2
        actual.getExtUuid2IdMapping(DataType.EXT_EM_INPUT).size() == 0
        actual.getExtUuid2IdMapping(DataType.EXT_GRID_RESULT).size() == 2
        actual.getExtUuid2IdMapping(DataType.EXT_PARTICIPANT_RESULT).size() == 0
    }
}