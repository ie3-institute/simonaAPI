# External simulation

An external simulation needs to extend the class `edu.ie3.simona.api.simulation.ExtSimulation`. This class extends the
java `Runnable` interface, because each external simulation is running in its own thread. The class contains the code to
run the external simulation and to exchange messages with SIMONA.

It will provide the following three abstract methods, that needs to be implemented by the subclass:
1. [initialize](#initializing-the-external-simulation)
2. [doActivity](#performing-an-activity)
3. [getDataConnections](#returning-the-data-connections)

There is also a `terminate` method that receives information is the simulation was successfully finished. This method
can be used e.g. to release resources, that are used by your external simulation.


## Initializing the external simulation

This method is called to initialize the external simulation. In this step, every data or connection, that is needed, is
loaded and/or set up.

After everything is initialized, this method should return the first tick, for which this external simulation should be
triggered by SIMONA.


## Performing an activity

The method `doActivity(long tick)` is called for every tick this external simulation should perform an activity. This
method should provide a new tick as long as there are future activities.


## Returning the data connections

The method `getDataConnections` returns all [data connections](/connections/connections) that are used to connect the
external simulation to SIMONA.
