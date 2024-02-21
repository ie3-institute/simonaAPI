/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.simona.api.data.ExtDataSimulation;

/**
 * An external simulation that needs results from SIMONA should implement this interface and handle
 * the ExtResultsData that is handed over.
 */
public interface ExtResultDataSimulation extends ExtDataSimulation {
  ResultDataFactory getFactory();
}
