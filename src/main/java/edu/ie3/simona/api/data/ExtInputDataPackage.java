package edu.ie3.simona.api.data;

import java.util.Map;

public interface ExtInputDataPackage extends ExtDataPackage {
    Map<String, ExtInputDataValue> getSimonaInputMap();
}
