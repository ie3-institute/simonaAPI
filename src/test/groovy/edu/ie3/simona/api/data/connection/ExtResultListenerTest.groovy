package edu.ie3.simona.api.data.connection


import edu.ie3.simona.api.ontology.results.ProvideResultEntities
import edu.ie3.simona.api.ontology.results.ResultDataResponseMessageToExt
import edu.ie3.simona.api.test.common.DataServiceTestData
import spock.lang.Specification

class ExtResultListenerTest extends Specification implements DataServiceTestData {

    class BasicListener extends ExtResultListener {
        List<ResultDataResponseMessageToExt> results = new ArrayList<>()

        @Override
        void processResponse(ResultDataResponseMessageToExt msg) {
            results.add(msg)
        }

        @Override
        void close() {}
    }

    def "An ExtResultListener should receive any result correctly"() {
        given:
        ExtResultListener listener = new BasicListener()

        when:
        listener.handleResponseMsg(new ProvideResultEntities([loadResult]))

        then:
        ProvideResultEntities message = listener.results.getFirst()

        message.results() == [(inputUuid): [loadResult]]
    }
}
