package edu.ie3.simona.api.data;

import java.util.HashMap;
import java.util.Map;

public class ExtInputDataPackage implements ExtDataPackage {
    private final Map<String, ExtInputDataValue> dataMap;

    public ExtInputDataPackage(Map<String, ExtInputDataValue> dataMap) {
        this.dataMap = dataMap;
    }

    public ExtInputDataPackage() {
        this(new HashMap<>());
    }

    public Map<String, ExtInputDataValue> getSimonaInputMap() {
        return dataMap;
    }

    public void addValue(String id, ExtInputDataValue value) {
        dataMap.put(id, value);
    }
}
