# Setup data

The setup data contains all information that is needed to set up an external simulation. This class is provided to the
`setup` method of the [ExtLinkInterface](/extlinkinterface). Additionally, this data is also added to the external simulation
by SIMONA.

Currently, the setup data holds the following information:
1. CLI arguments with which SIMONA is initiated.
2. The config that was parsed by SIMONA.
3. The electrical grid in the form of a grid container. ({doc}`PowerSystemDataModel - Grid Container <psdm:models/input/grid/gridcontainer>`)
