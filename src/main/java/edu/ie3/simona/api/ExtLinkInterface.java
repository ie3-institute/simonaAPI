/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api;

import edu.ie3.simona.api.data.SetupData;

/**
 * Every addon for SIMONA has to provide a class which implements one of the sub-interfaces of this interface.
 */
public sealed interface ExtLinkInterface permits ExtListenerProvider, ExtSimulationProvider {

  /**
   * Method to set up an addon. Everything that needs to be set up before an external
   * simulation or external listeners can be retrieved should be done here.
   *
   * @param data used for setting up the addon.
   */
  void setup(SetupData data);
}
