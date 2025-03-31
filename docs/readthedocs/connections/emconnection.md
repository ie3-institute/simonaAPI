# Energy management data connection

This data connection can be used to provide SIMONA with set-points for the {doc}`energy management units <psdm:models/input/em>`.

Every energy management data connection needs to have an [external entity mapping](/simulations/mapping).
This information is used to:
- associate a set-point value with an energy management unit in SIMONA.
- tell SIMONA which energy management units will receive external data


Currently, all energy management units have to receive a set-point from the same external simulation. Therefore, only
one external simulation can provide em data at once.


There are two methods to provide SIMONA with em data:
1. convertAndSend
2. provideEmData

The first method should be used, if your data is mapped by `strings`. If your data is already mapped by `UUID`, you can
use the second method.
