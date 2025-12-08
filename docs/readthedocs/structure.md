# API Structure

This page is mend to provide an overview of the structure of this API.

The API is split into the following parts:
1. [Data](#data)
2. [Exceptions](#exceptions)
3. [Mapping](#mapping)
4. [Ontology](#ontology)
5. [Simulation](#simulation)
6. [Uncategorized](#uncategorized)

## Data

The data package and its sub-packages contains all data-related classes. These are among other things [data connections](/connections/connections),
data containers and models.


## Exceptions

This package contains some exceptions. These can be thrown by the classes of this API.


## Mapping

This package contains the `ExtEntityMapping` as well as some `DataTypes` and the `ExtEntityEntry` that are both used in
the mapping.

## Ontology

All messages that can be exchanged between SIMONA and an external simulation resp. data connection are placed in this
package.


## Simulation

The simulation package contains the available simulations. Currently, only base classes that needs to be implemented are
provided. The most important class is the [ExtSimulation](/simulations/externalsimulation) as it is the base class for
all external simulations.


## Uncategorized

There is currently only one interface that is not part of a category. This is the [ExtLinkInterface](/extlinkinterface)
that is used for loading external simulations.
