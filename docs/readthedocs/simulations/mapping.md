# External entity mapping

This page contains information about the external entity mapping, that is used to map the identifiers of an external
simulation to the identifiers used by SIMONA. For each mapping entry, the data, that will be exchanged, is also specified.


## Column schemes

The column scheme defines which data can be exchanged by a [data connection](/connections/connections). The available
schemes are defined in the PowersystemDataModel (see: {doc}`Available Classes <psdm:models/input/additionaldata/timeseries>`).


## Data types

The data type contains information about the data connection, the [externals entity entry](#external-entity-entry)
is used for.

Currently, the following types exist:
- primary input
- em input
- grid result
- participant result


## External entity entry

Defines the mapping between an externally used `string` and an `uuid` used by SIMONA.

### Attributes and Remarks

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1


   * - Attribute
     - Remarks

   * - uuid
     - Identifier used by SIMONA

   * - id
     - Identifier used by the external simulation

   * - columnScheme
     - Information which data is exchanged

   * - dataType
     - Defines the data type

```

## Sources

The sources for the external entity mapping is defined in the same format as the PowerSystemDataModel (see: 
{doc}`I/O <psdm:io/basiciousage>`). 

Currently, only a csv source exist for the external entity mapping.
