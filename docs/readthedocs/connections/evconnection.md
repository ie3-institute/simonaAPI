# Electric vehicle data connection

This data connection can be used to connect an external ev movement service (e.g.: the
[MobilitySimulator](https://github.com/ie3-institute/MobilitySimulator)) to SIMONA.

The data connection can request the following information from SIMONA:
- the publicly available {doc}`electric vehicle charging stations <psdm:models/input/participant/evcs>`
- the current prices at every charging station
- a list of all departing electric vehicles

The data connection provides SIMONA with a list of electric vehicles at every charging station.
