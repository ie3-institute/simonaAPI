/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.ExtDataSimulation;
import edu.ie3.simona.api.data.ExtInputDataPackage;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryData;
import edu.ie3.simona.api.data.primarydata.PrimaryDataFactory;
import edu.ie3.simona.api.exceptions.ConvertionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An external simulation that provides primary data should implement this interface and handle the
 * ExtPrimaryData that is handed over.
 */
public class ExtEmDataSimulation implements ExtDataSimulation {

  private final List<UUID> controlledEms;

  private final Map<String, UUID> extEmMapping;

  private final EmDataFactory emDataFactory;

  private ExtEmData extEmData;

  public ExtEmDataSimulation(
          EmDataFactory emDataFactory,
          Map<String, UUID> extEmMapping
  ) {
    this.emDataFactory = emDataFactory;
    this.extEmMapping = extEmMapping;
    this.controlledEms = extEmMapping.values().stream().toList();
  }

  /** Hand over an ExtPrimaryData which enables communication regarding primary data. */
  public void setExtEmData(ExtEmData extEmData) {
    this.extEmData = extEmData;
  }

  public EmDataFactory getEmDataFactory() {
    return emDataFactory;
  }

  public List<UUID> getControlledEms() {
    return controlledEms;
  }

  public ExtEmData getExtEmData() {
    return extEmData;
  }

  public Map<UUID, PValue> createExtEmDataMap(
          ExtInputDataPackage extEmData
  ) {
    Map<UUID, PValue> emDataForSimona = new HashMap<>();
    extEmData.getSimonaInputMap().forEach(
            (id, extInput) -> {
              if (extEmMapping.containsKey(id)) {
                  try {
                      emDataForSimona.put(
                              extEmMapping.get(id),
                              emDataFactory.convert(extInput)
                      );
                  } catch (ConvertionException e) {
                      throw new RuntimeException(e);
                  }
              }
            }
    );
    return emDataForSimona;
  }


}
