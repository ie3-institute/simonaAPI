# API Structure

This page is mend to provide an overview of the structure of this API.

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

All messages that can be exchanged between SIMONA and an external simulation resp. data connection are placed in this
package.


## Simulation

The simulation package contains the class [ExtSimulation](/simulations/externalsimulation), which is the base class for all external simulations. Besides
that class it also contains extensions of [ExtSimulation](/simulations/externalsimulation). These other simulations provide additional methods. The
external simulation class will be provided to SIMONA via the [ExtLinkInterface](/extlinkinterface).


## External link interface

The [ExtLinkInterface](/extlinkinterface) that is used for providing external simulations is not part of any category, due to its special
function.
