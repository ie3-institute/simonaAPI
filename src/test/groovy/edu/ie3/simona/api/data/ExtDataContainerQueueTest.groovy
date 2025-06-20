package edu.ie3.simona.api.data

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.container.ExtInputContainer
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.util.concurrent.TimeUnit

class ExtDataContainerQueueTest extends Specification {

    @Shared
    private UUID uuid = UUID.fromString("07a724f6-c226-4826-87a6-e9ae42b734b0")

    @Shared
    private Value value = new PValue(Quantities.getQuantity(10, PowerSystemUnits.KILOWATT))

    def "An ExtDataContainerQueue should queue data as expected"() {
        given:
        def queue = new ExtDataContainerQueue()
        def data = new ExtInputContainer(0L, 3600L)

        when:
        queue.queueData(data)

        then:
        queue.size() == 1
    }

    def "An ExtDataContainerQueue should take a container as expected"() {
        given:
        def queue = new ExtDataContainerQueue()
        def data = new ExtInputContainer(0L, 3600L)
        queue.queueData(data)

        when:
        def container = queue.takeContainer()

        then:
        container == data
        queue.size() == 0
    }

    def "An ExtDataContainerQueue should poll no container, if queue remains empty"() {
        given:
        def queue = new ExtDataContainerQueue()

        when:
        def container = queue.pollContainer(10, TimeUnit.MILLISECONDS)

        then:
        container == Optional.empty()
        queue.size() == 0
    }

    def "An ExtDataContainerQueue should poll a container, if queue contains data"() {
        given:
        def queue = new ExtDataContainerQueue()
        def data = new ExtInputContainer(0L, 3600L)
        queue.queueData(data)

        when:
        def container = queue.pollContainer(10, TimeUnit.MILLISECONDS)

        then:
        container == Optional.of(data)
        queue.size() == 0
    }

    def "An ExtDataContainerQueue should take part of a container as expected"() {
        given:
        def queue = new ExtDataContainerQueue()
        def data = new ExtInputContainer(0L, 3600L)
        data.addPrimaryValue(uuid, value)

        queue.queueData(data)

        when:
        def primaryData = queue.takeData(ExtInputContainer::extractPrimaryData)

        then:
        primaryData == [(uuid): value]
        queue.size() == 0
    }

    def "An ExtDataContainerQueue should poll no part of a container, if queue remains empty"() {
        given:
        def queue = new ExtDataContainerQueue()

        when:
        def primaryData = queue.pollData(ExtInputContainer::extractPrimaryData, 10, TimeUnit.MILLISECONDS)

        then:
        primaryData == Optional.empty()
        queue.size() == 0
    }

    def "An ExtDataContainerQueue should poll part of a container, if queue contains data"() {
        given:
        def queue = new ExtDataContainerQueue()
        def data = new ExtInputContainer(0L, 3600L)
        data.addPrimaryValue(uuid, value)

        queue.queueData(data)

        when:
        def primaryData = queue.pollData(ExtInputContainer::extractPrimaryData, 10, TimeUnit.MILLISECONDS)

        then:
        primaryData == Optional.of([(uuid): value])
        queue.size() == 0
    }

}
