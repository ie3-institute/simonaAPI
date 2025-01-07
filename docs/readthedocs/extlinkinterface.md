# External link interface

The external link interface, or short `ExtLinkInterface`, is used to provide SIMONA with all methods necessary to load
the external simulation.

There are currently these methods:
1. [setup](#setting-up-the-external-simulation)
2. [getExtSimulation](#getting-the-external-simulation)

Both method will be called by SIMONA to set up and get the external simulation.


## Setting up the external simulation

This method receives an [external simulation adapter data](/simulations/extsimadapterdata) that contains the following
information:
- CLI arguments SIMONA is initiated with
- Reference to the actor that handles scheduler control flow in SIMONA
- Queue with triggers the external simulation needs to handle

The method can use these data, if necessary, to set up the external simulation. In all cases, this method needs to
forward the adapter data to the external simulation.


## Getting the external simulation

This method should return the [external simulation](/simulations/externalsimulation).
