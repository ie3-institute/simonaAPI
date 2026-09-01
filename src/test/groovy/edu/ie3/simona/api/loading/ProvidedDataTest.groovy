package edu.ie3.simona.api.loading

import edu.ie3.simona.api.data.connection.ExtDataConnection
import edu.ie3.simona.api.data.connection.ExtResultDataConnection
import edu.ie3.simona.api.data.connection.ExtResultListener
import edu.ie3.simona.api.exceptions.ExtSimException
import edu.ie3.simona.api.ontology.results.ResultDataResponseMessageToExt
import edu.ie3.simona.api.simulation.ExtSimulation
import spock.lang.Specification

class ProvidedDataTest extends Specification {

    def "A ProvidedData can be constructed correctly"() {
        when:
        def data = new ProvidedData()

        then:
        data.extSimulations().empty
        data.dataConnections().empty
    }

    def "An external simulation can be added to a ProvidedData correctly"() {
        given:
        def data = new ProvidedData()
        def connection = new ExtResultDataConnection(null)

        ExtSimulation extSim = new ExtSimulation("dummy") {
            @Override
            protected long initialize() {
                return 0
            }

            @Override
            protected OptionalLong doActivity(long tick) throws ExtSimException, InterruptedException {
                return null
            }

            @Override
            Set<ExtDataConnection> getDataConnections() {
                return Set.of(connection)
            }
        }

        when:
        data.add(extSim)

        then:
        data.extSimulations().size() == 1
        data.extSimulations() == [extSim] as Set

        data.dataConnections().size() == 1
        data.dataConnections() == [connection] as Set
    }

    def "An external result listener can be added to a ProvidedData correctly"() {
        given:
        def data = new ProvidedData()

        ExtResultListener listener = new ExtResultListener() {
            @Override
            void processResponse(ResultDataResponseMessageToExt msg) {}

            @Override
            void close() {}
        }

        when:
        data.add([listener])

        then:
        data.extSimulations().empty

        data.dataConnections().size() == 1
        data.dataConnections() == [listener] as Set
    }


}
