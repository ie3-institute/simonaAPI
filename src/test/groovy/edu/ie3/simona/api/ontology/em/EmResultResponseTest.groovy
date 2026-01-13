package edu.ie3.simona.api.ontology.em

import edu.ie3.simona.api.data.model.em.FlexOptionRequest
import spock.lang.Specification

class EmResultResponseTest extends Specification {

    def "An EmResultResponse can be build correctly"() {
        given:
        def data = [(UUID.randomUUID()): [new FlexOptionRequest(UUID.randomUUID(), false)]]

        when:
        def response = new EmResultResponse(data)

        then:
        response.emResults == data
    }

}
