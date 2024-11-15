/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api;

import edu.ie3.simona.api.data.ExtDataConnection;
import edu.ie3.simona.api.simulation.ExtSimulation;
import java.util.List;

/**
 * Every external simulation has to provide a class {@code edu.ie3.simona.api.ExtLink} which
 * implements this interface.
 */
public interface ExtLinkInterface {
  /** Returns the external simulation. */
  ExtSimulation getExtSimulation();

  /** Returns the data connection between this external simulation and SIMONA. */
  default List<ExtDataConnection> getExtDataConnections() {
    return getExtSimulation().getDataConnections();
  }
}
