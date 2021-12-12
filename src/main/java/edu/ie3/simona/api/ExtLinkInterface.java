/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api;

import edu.ie3.simona.api.data.ExtDataSimulation;
import edu.ie3.simona.api.simulation.ExtSimulation;
import java.util.List;

/**
 * Every external simulation has to provide a class {@code edu.ie3.simona.api.ExtLink} which
 * implements this interface.
 *
 * <p>{@link #getExtSimulation()} and {@link #getExtDataSimulations()} can return references to the
 * same object, if that object implements both {@link ExtSimulation} and one or more variants of
 * {@link ExtDataSimulation}.
 */
public interface ExtLinkInterface {
  ExtSimulation getExtSimulation();

  List<ExtDataSimulation> getExtDataSimulations();
}
