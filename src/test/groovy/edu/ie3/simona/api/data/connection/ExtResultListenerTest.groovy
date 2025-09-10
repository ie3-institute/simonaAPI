package edu.ie3.simona.api.data.connection

import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.simona.api.ontology.results.ProvideResultEntities
import edu.ie3.simona.api.exceptions.UnexpectedResponseMessageException
import edu.ie3.simona.api.test.common.DataServiceTestData
import spock.lang.Specification

class ExtResultListenerTest extends Specification implements DataServiceTestData {

    def "An ExtResultListener should receive any result correctly"() {
        given:
        ExtResultListener listener = new ExtResultListener()

        when:
        listener.queueExtResponseMsg(new ProvideResultEntities([loadResult]))

        then:
        ProvideResultEntities message = listener.receiveAny()

        message.results() == [(inputUuid): [loadResult]]
    }

    def "An ExtResultListener should receive a specific result correctly"() {
        given:
        ExtResultListener listener = new ExtResultListener()

        when:
        listener.queueExtResponseMsg(new ProvideResultEntities([loadResult]))

        then:
        def message = listener.receiveWithType(ProvideResultEntities)

        message.results() == [(inputUuid): [loadResult]]
    }

    def "An ExtResultListener should thrown an exception if the wrong type is received"() {
        given:
        ExtResultListener listener = new ExtResultListener()

        when:
        listener.queueExtResponseMsg(new ProvideResultEntities([loadResult]))
        listener.receiveWithType(NodeResult)

        then:
        thrown(UnexpectedResponseMessageException)
    }
}
