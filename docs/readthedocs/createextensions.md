# How to create extensions for SIMONA

This page contains all information that are needed to create an extension for SIMONA. An extension can contain one or more
of the following elements:

- External Simulation
- One or more external result listener(s)


## Basic project setup

Here are some steps to perform in order to create an extension:

1. Create a project in a Java-compatible language and add this API as a dependency.
2. Create the external simulations, listeners, etc. that should be provided by the extension.
3. Add a class that implements all needed sub-interfaces of [ExtLinkInterface](/extlinkinterface).
4. Optionally: implement the `setup` method to set up all the external simulations, listeners, etc.
5. Implement the methods that are defined by the sub-interfaces of [ExtLinkInterface](/extlinkinterface).
6. Define the Java service (see: [](#defining-the-service)).
7. Create a fat Jar.
8. Add the Jar to your SIMONA simulation.


## Defining the service

To define a service for an external simulation you need to create the following path inside your project:
`resources/META-INF/services/edu.ie3.simona.api.ExtLinkInterface`. Inside this file you need to add the path to your implementation
of `ExtLinkInterface`.


## Project with multiple external simulations
Your project could contain multiple external simulations by creating multiple implementations of the `ExtLinkInterface`
and adding them to the same service file.


## Limitations to consider when creating extensions

There is some limitations to this approach.

1. Some external simulations such as for EV or EM can only be provided once.
2. Each asset in SIMONA can only **receive** external data from **one** external data connection. The primary data connection
has the lowest priority and is ignored, if another data connection is already providing data. Also, if multiple primary data
connection are set up to provide data to one asset, SIMONA will throw an exception.
