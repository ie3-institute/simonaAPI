# External Co-Simulation

This abstract class is an extension to the [external simulation](/simulations/externalsimulation) class. It is primarily
used to couple SIMONA with co-simulation frameworks. The idea behind this class is the simplification of adding SIMONA
to these frameworks.

## Co-Simulation frameworks

Currently, SIMONA can be used with the following co-simulation frameworks:
1. [MOSAIk](#mosaik)
2. [OpSim](#opsim)


### MOSAIK
[MOSAIK](https://mosaik.offis.de/) is a co-simulation framework from [Offis e.V.](https://www.offis.de/). To connect
SIMONA with MOSAIK see [simosaik](https://github.com/ie3-institute/simosaik).


### OpSim
[OpSim](https://www.iee.fraunhofer.de/en/schnelleinstieg-wirtschaft/themen/opsim-homepage.html) is an test- and simulation-environment
for grid control and aggregation strategies from [Fraunhofer IEE](https://www.iee.fraunhofer.de/en.html). To connect
SIMONA with OpSim see [simopsim](https://github.com/ie3-institute/simopsim).


## Connectivity

In order to simplify the connection, this class provides two data queues. One queue is used to provide SIMONA with
external values. The other is used to provide the co-simulation framework with value calculated by SIMONA.


## Functionality

There are some helpful methods to create some [data connections](/connections/connections) and to exchange data between
SIMONA and the co-simulation framework.
