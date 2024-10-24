package edu.ie3.simona.api.test.common

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.system.LoadResult
import edu.ie3.datamodel.models.value.PValue
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

trait DataServiceTestData {
    UUID inputUuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
    PValue pValue = new PValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN))

    LoadResult loadResult = new LoadResult(
            ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"),
            inputUuid,
            Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN),
            Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
    )
}