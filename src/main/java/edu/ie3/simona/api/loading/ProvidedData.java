/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.loading;

import edu.ie3.simona.api.data.connection.ExtDataConnection;
import edu.ie3.simona.api.data.connection.ExtResultListener;
import edu.ie3.simona.api.module.ExtModule;
import edu.ie3.simona.api.module.ModuleType;
import edu.ie3.simona.api.simulation.ExtSimulation;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Container class for all {@link ExtSimulation}s and {@link ExtDataConnection}s provided by addons.
 */
public final class ProvidedData {
  private final Set<ExtSimulation> extSimulations = new HashSet<>();
  private final EnumMap<ModuleType, ExtModule> extModules = new EnumMap<>(ModuleType.class);
  private final Set<ExtResultListener> listeners = new HashSet<>();

  public static ProvidedData empty() {
    return new ProvidedData();
  }

  public Set<ExtSimulation> extSimulations() {
    return new HashSet<>(extSimulations);
  }

  public EnumMap<ModuleType, ExtModule> extModules() {
    return new EnumMap<>(extModules);
  }
  
  public Set<ExtResultListener> extListeners() {
    return new HashSet<>(this.listeners);
  }

  void add(ExtSimulation extSimulation) {
    extSimulations.add(extSimulation);
  }

  void add(EnumMap<ModuleType, ExtModule> extModuleMap) {
    for (var e: extModuleMap.entrySet()) {
      ModuleType type = e.getKey();

      if (extModules.containsKey(type)) {
        throw new IllegalArgumentException("A second external module of type '"+type+"' was found. Only one module per type is allowed!");
      }

      extModules.put(type, e.getValue());
    }
  }

  void add(Collection<ExtResultListener> extListeners) {
    listeners.addAll(extListeners);
  }

  public void add(ProvidedData that) {
    extSimulations.addAll(that.extSimulations);
    add(that.extModules);
    listeners.addAll(that.listeners);
  }
}
