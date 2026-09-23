/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api;

import edu.ie3.simona.api.data.connection.ExtResultListener;
import edu.ie3.simona.api.exceptions.ExtDataConnectionException;
import java.util.Set;

/** This interface needs to be implemented to provide SIMONA with external listeners. */
public non-sealed interface ExtListenerProvider extends ExtLinkInterface {

  /** Returns the ext result listeners. */
  default Set<ExtResultListener> getResultListeners() {
    throw new ExtDataConnectionException("No external result listeners provided!");
  }
}
