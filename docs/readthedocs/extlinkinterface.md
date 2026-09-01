# External link interface

The external link interface, or short `ExtLinkInterface`, is used to provide SIMONA with all methods necessary to load
addons. The interface itself contains a [setup](#setting-up-the-external-simulation) method, while all other methods are
defined in the implementations.

There are currently two interfaces that extend `ExtLinkInterface`:
1. [ExtSimulationProvider](#external-simulation-provider)
2. [ExtListenerProvider](#external-listener-provider)

The addon needs to provide at least one class that implements at least one of these interfaces. It is possible to extend
all implementations at the same time.

To make the class available to SIMONA, the full classpath needs to be put into the file `../resources/META-INF/services/edu.ie3.simona.api.ExtLinkInterface`.


## Setting up the external simulation

Before calling a method of a provider, SIMONA will first call the `setup` method. This method receives a
[setup data](/data/setupdata) that contains all needed information for setting up the external simulation. The method
can use these data, if necessary, to set up the external simulation.

If no setup is needed, there is no need to implement this method.

## External simulation provider

The external simulation provider defines the method `getExtSimulation` that will return one [external simulation](/simulations/externalsimulation).


## External listener provider

The external listener provider defines the method `getResultListeners` that will return a set of external result listeners.
