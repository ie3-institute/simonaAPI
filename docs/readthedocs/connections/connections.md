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

Each input connection has an actor reference to the data service in SIMONA as well as an actor reference to the adapter
that handles the scheduler control flow in SIMONA.

## Result data connections
