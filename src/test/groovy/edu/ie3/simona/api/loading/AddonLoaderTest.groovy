package edu.ie3.simona.api.loading


import edu.ie3.simona.api.ExtListenerProvider
import edu.ie3.simona.api.ExtSimulationProvider
import edu.ie3.simona.api.data.SetupData
import edu.ie3.simona.api.data.connection.ExtDataConnection
import edu.ie3.simona.api.data.connection.ExtResultDataConnection
import edu.ie3.simona.api.data.connection.ExtResultListener
import edu.ie3.simona.api.exceptions.ExtSimException
import edu.ie3.simona.api.ontology.results.ResultDataResponseMessageToExt
import edu.ie3.simona.api.simulation.ExtSimulation
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

class AddonLoaderTest extends Specification {

    @Shared
    private ExtResultDataConnection connection = new ExtResultDataConnection(null)

    @Shared
    private ExtSimulation extSim = new ExtSimulation("dummy") {
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

    @Shared
    private ExtResultListener listener = new ExtResultListener() {
        @Override
        void processResponse(ResultDataResponseMessageToExt msg) {}

        @Override
        void close() {}
    }

    private class Provider implements ExtSimulationProvider, ExtListenerProvider {

        @Override
        void setup(SetupData data) {}

        @Override
        Set<ExtResultListener> getResultListeners() {
            return Set.of(listener)
        }

        @Override
        ExtSimulation getExtSimulation() {
            return extSim
        }
    }

    def "The AddonLoader sets up an addon correctly"() {
        given:
        def setupData = new SetupData(null, null, null, null, null)

        when:
        def data = AddonLoader.setUpExtLink(new Provider(), setupData)

        then:
        data.extSimulations().size() == 1
        data.extSimulations() == [extSim] as Set
        data.dataConnections().size() == 2
        data.dataConnections() == [connection, listener] as Set
    }

    def "The AddonLoader builds the directory correctly"() {
        given:
        Path path = Path.of(".")

        when:
        def absolutePath = AddonLoader.buildDir(path)

        then:
        absolutePath == path.toAbsolutePath().toFile()
    }

    def "The AddonLoader scans a directory correctly"() {
        given:
        Path path = Path.of("src", "test", "resources", "edu", "ie3", "simona", "api", "simulation", "mapping")

        when:
        def files = AddonLoader.scanDirectory(path, ["csv"] as Set).collect { it -> it.getName() }

        then:
        files == ["ext_entity_mapping.csv"]
    }

}
