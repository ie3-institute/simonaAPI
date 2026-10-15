# API Structure

This page is meant to provide an overview of the structure of this API.

The API is split into the following parts:
1. [Data](#data)
2. [Exceptions](#exceptions)
3. [Mapping](#mapping)
4. [Ontology](#ontology)
5. [Simulation](#simulation)
6. [External link interface](#external-link-interface)

## Data

The data package and its sub-packages contains all data-related classes. These are among other things [data connections](/data/connections),
data containers and models.


## Exceptions

This package contains some exceptions. These can be thrown by the classes of this API.


## Mapping

This package contains the `ExtEntityMapping` as well as some `DataTypes` and the `ExtEntityEntry` that are both used in
the mapping.

## Ontology

All messages that can be exchanged between SIMONA and an extension are placed in this package.


## Simulation

The simulation package contains the class [ExtSimulation](/simulations/externalsimulation), which is the base class for all external simulations,
as well as some extensions. These other simulations provide additional methods. The external simulation class will be provided to SIMONA via
an [ExtSimulationProvider](/extlinkinterface#external-simulation-provider).


## External link interface

The [ExtLinkInterface](/extlinkinterface) is the base interface used for providing extensions, such as external simulations,
to SIMONA. This interface and its sub-interfaces are not part of any category, due to their special function.
