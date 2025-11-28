/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import com.typesafe.config.Config;
import edu.ie3.datamodel.models.input.container.GridContainer;

/**
 * Container class that holds all data needed for setting up an external simulation.
 *
 * @param mainArgs CLI arguments with which SIMONA is initiated.
 * @param config The parsed simona config.
 * @param gridContainer The electrical grid.
 */
public record SetupData(String[] mainArgs, Config config, GridContainer gridContainer) {}
