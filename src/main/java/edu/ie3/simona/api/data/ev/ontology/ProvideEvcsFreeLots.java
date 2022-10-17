/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Provides the number of available lots per charging station to the external simulation as a
 * response to a {@link RequestEvcsFreeLots}.
 *
 * @param evcs the number of free lots per charging station UUID
 */
public record ProvideEvcsFreeLots(Map<UUID, Integer> evcs) implements EvDataResponseMessageToExt {

  /** No EVCS lots available */
  public ProvideEvcsFreeLots() {
    this(new HashMap<>(0));
  }
}
