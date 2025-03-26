# How to create external simulations for SIMONA

This page contains all information on how to create external simulations.

SIMONA uses the java service loader, to load external simulations. Each external simulations needs to have an
implementation of the [ExtLinkInterface](/extlinkinterface), that is provided to the service loader as a service.


## Defining the service

To define a service for an external simulation you need to create the following path inside your project:
`resources/META-INF/services/edu.ie3.simona.api.ExtLinkInterface`. Inside this file you need to add the implementation
of the `ExtLinkInterface`.


## Project with multiple external simulations
Your project could contain multiple external simulations by creating multiple implementations of the `ExtLinkInterface`
and adding them to the service file. When doing this SIMONA will give you a warning. 


**Also**, each asset in SIMONA can only receive external data from **one** external simulation. If two external simulations
are set up to provide data to the same asset, SIMONA will exit with an exception. Therefore, it is encouraged to create
an own project for each external simulation.
