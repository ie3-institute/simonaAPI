/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev;

import edu.ie3.simona.api.data.ExtDataSimulation;

/**
 * An external simulation that provides an ev mobility simulation should implement this interface
 * and handle the ExtEvData that is handed over.
 */
@Deprecated
public interface ExtEvSimulation extends ExtDataSimulation {

  /**
   * Hand over an ExtEvData which enables communication regarding ev movements.
   *
   * @param evData the ev data
   */
  void setExtEvData(ExtEvDataConnection evData);
}
