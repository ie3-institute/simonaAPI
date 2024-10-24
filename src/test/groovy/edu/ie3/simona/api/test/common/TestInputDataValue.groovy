package edu.ie3.simona.api.test.common

import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.simona.api.data.ExtInputDataValue


class TestInputDataValue implements ExtInputDataValue {
    private final Value value

    TestInputDataValue(Value value) {
        this.value = value
    }

    Value getValue() {
        return value
    }
}
