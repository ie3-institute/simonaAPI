/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.exceptions;

import edu.ie3.simona.api.data.ExtDataConnection;

public class ExtDataConnectionException extends RuntimeException {

  public ExtDataConnectionException(Class<? extends ExtDataConnection> connectionClass) {
    this(
        "The external data connection '"
            + connectionClass.getSimpleName()
            + "' could not be build!");
  }

  public ExtDataConnectionException(final String message) {
    super(message);
  }
}
