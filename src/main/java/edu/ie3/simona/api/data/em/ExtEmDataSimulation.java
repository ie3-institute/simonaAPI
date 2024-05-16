/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.simona.api.data.ExtDataSimulation;
import edu.ie3.simona.api.data.primarydata.ExtPrimaryData;
import edu.ie3.simona.api.data.primarydata.PrimaryDataFactory;

import java.util.List;
import java.util.UUID;

/**
 * An external simulation that provides primary data should implement this interface and handle the
 * ExtPrimaryData that is handed over.
 */
public class ExtEmDataSimulation implements ExtDataSimulation {

  private final List<UUID> controlledEms;

  private final EmDataFactory emDataFactory;

  private ExtEmData extEmData;

  public ExtEmDataSimulation(
          EmDataFactory emDataFactory,
          List<UUID> controlledEms
  ) {
    this.emDataFactory = emDataFactory;
    this.controlledEms = controlledEms;
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
}
