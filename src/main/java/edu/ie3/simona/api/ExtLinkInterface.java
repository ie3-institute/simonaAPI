/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api;

import edu.ie3.simona.api.simulation.ExtSimAdapterData;
import edu.ie3.simona.api.simulation.ExtSimulation;

/**
 * Every external simulation has to provide a class {@code edu.ie3.simona.api.ExtLink} which
 * implements this interface.
 */
public interface ExtLinkInterface {
  /** Returns the external simulation. */
  ExtSimulation getExtSimulation();

  /**
   * Method to set up an external simulation. Everything that needs to be set up before the
   * external simulation can be retrieved should be done here.
   * @param data used for setting up the external simulation
   */
  void setup(ExtSimAdapterData data);
}
