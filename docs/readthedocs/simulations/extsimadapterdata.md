# External simulation adapter data

The adapter data contains the following:
1. Reference to the actor that handles the scheduler control flow in SIMONA
2. The CLI arguments SIMONA is started with
3. A blocking queue with triggers, that the external simulation needs to handle

This class also contains two method to exchange messages with SIMONA.
1. queueExtMsg: This method is called by SIMONA to provide the external simulation with a control message
2. send: This method is used to send a response message to SIMONA as an answer to a control message
