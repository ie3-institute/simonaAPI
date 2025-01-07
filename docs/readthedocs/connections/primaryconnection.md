# Primary data connection

This data connection can be used to provide SIMONA with primary data for input models.

Every primary data connection needs to have an [external entity mapping](/simulations/mapping).
This information is used to:
- tell SIMONA which asset will receive primary data from this connection
- associate a primary value with an asset in SIMONA.


Different assets (e.g.: two loads) can receive primary data from different primary data connections and therefore different
external simulations. The rule is, that one asset can only receive data from one connection. Another possibility is, that
different types of assets (e.g.: loads, pv-plants) can each have their own external simulation.
