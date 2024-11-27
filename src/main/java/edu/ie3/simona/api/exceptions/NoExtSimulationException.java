/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.exceptions;

import edu.ie3.simona.api.ExtLinkInterface;

public class NoExtSimulationException extends RuntimeException {

  public NoExtSimulationException(Class<? extends ExtLinkInterface> linkClass) {
    this("No external simulation was set up in ExtLinkInterface: ."+linkClass.getSimpleName());
  }

  public NoExtSimulationException(final String message) {
    super(message);
  }
}
