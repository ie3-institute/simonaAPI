# Data connections

This API defines some data connections in order to exchange data between SIMONA and an external simulation.

1. Input data connections
2. Output data connections
3. Bidirectional data connections
4. External simulation data connection

## Input data connections

These data connections are used to provide SIMONA with external data. Each input data connection contains two references
for SIMONA actors. The first reference specifies the SIMONA service that will receive the external data. The second
reference is for the external simulation adapter. This adapter receives a message to schedule the data service in SIMONA.

The process of sending data to the service and requesting scheduling is handled by the `sendExtMsg` method.
To send a message, simply call the method with the message as input.

Currently, the following input data connections are provided:
- ExtPrimaryDataConnection


## Output data connections

These data connections are used to provide SIMONA response messages to the external simulation. Each output data connection
has a queue for messages sent by SIMONA. The result data connection itself cannot send messages to SIMONA.

Currently, the following input data connections are provided:
- ExtResultListener


## Bidirectional data connections

The bidirectional data connection combines the functionality of both input and output data connections. These data connections
can be used to send data to SIMONA and receive responses. One additional feature is that bidirectional data connections
can be used to request responses, e.g. SIMONA results.

Currently, the following input data connections are provided:
- ExtEmDataConnection
- ExtEvDataConnection
- ExtResultDataConnection


## External simulation data connection

The external simulation data connection is a special connection, since it is only used send control messages between the
external simulation and SIMONA.

This class contains three method to exchange messages with SIMONA.
1. `queueExtMsg`: This method is called by SIMONA to provide the external simulation with a control message
2. `receive`: The method is used to retrieve a control message that was sent by SIMONA. 
3. `send`: This method is used to send a response message to SIMONA as an answer to a control message
