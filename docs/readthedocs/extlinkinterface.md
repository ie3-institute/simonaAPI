# External link interface

The external link interface, or short `ExtLinkInterface`, is used to provide SIMONA with all methods necessary to load
extensions. The interface itself contains a [setup](#setting-up-the-extensions) method, while all other methods are
defined in the implementations or extensions of the base interface.

There are currently two interfaces that extend `ExtLinkInterface`:
1. [ExtSimulationProvider](#external-simulation-provider)
2. [ExtListenerProvider](#external-listener-provider)

An extension needs to provide at least one class that implements at least one of these interfaces. It is possible to extend
all implementations at the same time.

To make the class available to SIMONA, the full classpath needs to be put into the file `../resources/META-INF/services/edu.ie3.simona.api.ExtLinkInterface`.


## Setting up the extensions

Before calling a method of a provider, SIMONA will first call the `setup` method. This method receives a
[setup data](/data/setupdata) that contains all needed information for setting up an extension. Implementing the method
is optional. If no setup is needed, there is no need to implement this method.

External simulations have a method `setSetupData`. This method is automatically called while loading an extension.
Therefor, there is no need call this method in the `setup` method.


## External simulation provider

The external simulation provider defines the method `getExtSimulation` that will return one [external simulation](/simulations/externalsimulation).


## External listener provider

The external listener provider defines the method `getResultListeners` that will return a set of external result listeners.
