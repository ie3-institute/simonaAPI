/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.ontology.DataResponseMessageToExt;

/**
 * Interface for a connection between SIMONA and an external simulation with data flow from SIMONA
 * to external.
 *
 * @param <T> type of response messages to ext
 */
public sealed interface ExtOutputDataConnection<T extends DataResponseMessageToExt>
    extends ExtDataConnection permits BiDirectional, ExtResultListener {

  /** Handles the response message send by SIMONA. */
  void handleResponseMsg(T msg) throws InterruptedException;
}
