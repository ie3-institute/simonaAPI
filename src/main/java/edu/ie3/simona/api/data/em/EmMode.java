package edu.ie3.simona.api.data.em;

import edu.ie3.simona.api.data.mapping.DataType;

public enum EmMode {
    SET_POINT("setPoint"),
    EM_COMMUNICATION("emCommunication"),
    EM_OPTIMIZATION("emOptimization"),;


    public final String mode;

    EmMode(String mode) {
        this.mode = mode;
    }

    public static EmMode fromDataType(DataType dataType) {
        return switch (dataType) {
            case EXT_EM_INPUT -> EmMode.SET_POINT;
            case EXT_EM_COMMUNICATION -> EmMode.EM_COMMUNICATION;
            case EXT_EM_OPTIMIZER -> EmMode.EM_OPTIMIZATION;
            default -> throw new IllegalStateException("Unexpected data type: " + dataType);
        };
    }
}
