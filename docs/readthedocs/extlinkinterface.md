# External link interface

The external link interface, or short `ExtLinkInterface`, is used to provide SIMONA with all methods necessary to load
the external simulation.

Currently, these methods exist:
1. [setup](#setting-up-the-external-simulation)
2. [getExtSimulation](#getting-the-external-simulation)

Both method will be called by SIMONA to initialize and retrieve the external simulation.


## Setting up the external simulation

Before calling the `getExtSimulation` method, SIMONA will first call the `setup` method. This method receives a
[setup data](/data/setupdata) that contains all needed information for setting up the external simulation. The method
can use these data, if necessary, to set up the external simulation.

If no setup is needed, there is no need to implement this method.

## Getting the external simulation

This method should return the [external simulation](/simulations/externalsimulation).
