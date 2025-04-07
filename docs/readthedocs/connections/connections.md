# Data connections

In order to exchange data between SIMONA and an external simulation, this API defines some data connections.

The data connections provided by the API can be divided into three kinds of data connections:
1. Input data connections
2. Output data connections
3. Bidirectional data connections

## Input data connections

These data connections are used to provide SIMONA with external data. Each input data connection contains two references
for SIMONA actors. The first reference is for the actual service within SIMONA that will receive the external data. The second
reference is for the external simulation adapter. The adapter will receive a message to schedule the data service in SIMONA.

The process of sending data to the service and asking for scheduling of the service is taken care of by the method `sendExtMsg`.
In order to send a message, simply call the method with the message as input.

Currently, the following input data connections are provided:
- ExtPrimaryDataConnection


## Output data connections

These data connections are used to provide SIMONA response messages to the external simulation. Each output data connection
has a queue for messages send by SIMONA. The result data connection itself cannot send messages to SIMONA.

Currently, the following input data connections are provided:
- ExtResultListener


## Bidirectional data connections

The bidirectional data connection combines the functionality of both input and output data connections. These data connections
can be used to send data to SIMONA and receive responses. Also, one additional feature is, that a bidirectional data connections
can request responses, e.g. SIMONA results.

Currently, the following input data connections are provided:
- ExtEmDataConnection
- ExtEvDataConnection
- ExtResultDataConnection
