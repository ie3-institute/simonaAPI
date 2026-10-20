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

class ExtensionLoaderTest extends Specification {

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

    def "The ExtensionLoader should load no Jars correctly"() {
        given:
        def path = Path.of(getClass().getResource("_no-jars").toURI())
        def setupData = new SetupData(null, null, null, null, null)

        when:
        def provided = new JarLoader().load(path, setupData)

        then:
        provided.extSimulations().size() == 0

    }

    def "The ExtensionLoader sets up an extension correctly"() {
        given:
        def setupData = new SetupData(null, null, null, null, null)

        when:
        def data = ExtensionLoader.setupExtLink(new Provider(), setupData)

        then:
        data.extSimulations().size() == 1
        data.extSimulations() == [extSim] as Set
        data.extListeners().size() == 1
        data.extListeners() == [listener] as Set
    }

    def "The ExtensionLoader builds the directory correctly"() {
        given:
        Path path = Path.of(".")

        when:
        def absolutePath = ExtensionLoader.buildDir(path)

        then:
        absolutePath == path.toAbsolutePath().toFile()
    }

    def "The ExtensionLoader scans a directory correctly"() {
        given:
        Path path = Path.of("src", "test", "resources", "edu", "ie3", "simona", "api", "simulation", "mapping")

        when:
        def files = ExtensionLoader.scanDirectory(path, ["csv"] as Set).collect { it -> it.getName() }

        then:
        files == ["ext_entity_mapping.csv"]
    }

}
