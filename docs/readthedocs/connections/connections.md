# Data connections

In order to send data to or to receive result from SIMONA, each external simulation needs to have at least one data
connection.


## Input data connections

These connections are used to provide SIMONA with data, that is calculated by the external simulation.

Currently, the following input connections exist:

```{toctree}
:maxdepth: 2

emconnection
evconnection
primaryconnection
```

Each input data connection has an actor reference to the data service in SIMONA as well as an actor reference to the
adapter that handles the scheduler control flow in SIMONA.

## Result data connections

The result data connection can be used to provide SIMONA results to the external simulation. Currently,
{doc}`node <psdm:models/result/grid/node>` and {doc}`participant <psdm:models/result/participant/systemparticipant>`
results are supported.

The result data connection has an actor reference to the data service in SIMONA, an actor reference to the data service
activation adapter as well as an actor reference to the adapter that handles the scheduler control flow in SIMONA.

Unlike the input data connections, the result data connection needs two [external entity mapping](/simulations/mapping)
information. These specify for which grid asset and/or system participant results should be provided by SIMONA.
