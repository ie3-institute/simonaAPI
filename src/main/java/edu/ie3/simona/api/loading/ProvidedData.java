/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.loading;

import edu.ie3.simona.api.data.connection.ExtDataConnection;
import edu.ie3.simona.api.data.connection.ExtResultListener;
import edu.ie3.simona.api.simulation.ExtSimulation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Container class for all {@link ExtSimulation}s and {@link ExtDataConnection}s provided by addons.
 */
public final class ProvidedData {
  private final Set<ExtSimulation> extSimulations = new HashSet<>();
  private final Set<ExtDataConnection> dataConnections = new HashSet<>();
  private final Set<ExtResultListener> listeners = new HashSet<>();

  public static ProvidedData empty() {
    return new ProvidedData();
  }

  public Set<ExtSimulation> extSimulations() {
    return new HashSet<>(extSimulations);
  }

  public Set<ExtDataConnection> dataConnections() {
    Set<ExtDataConnection> connections = new HashSet<>(dataConnections);
    connections.addAll(listeners);
    return connections;
  }

  void add(ExtSimulation extSimulation) {
    extSimulations.add(extSimulation);
    dataConnections.addAll(extSimulation.getDataConnections());
  }

  void add(Collection<ExtResultListener> extListeners) {
    listeners.addAll(extListeners);
  }

  void add(ProvidedData that) {
    extSimulations.addAll(that.extSimulations);
    dataConnections.addAll(that.dataConnections);
    listeners.addAll(that.listeners);
  }
}
