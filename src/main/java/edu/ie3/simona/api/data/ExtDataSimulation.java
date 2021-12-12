/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

/**
 * Represents a data flow inside the external simulation. An external simulation can operate
 * multiple data flows of different types. For each data type, there needs to be an interface
 * extending this interface which provides methods that receive a data adapter.
 *
 * <p>See {@link edu.ie3.simona.api.data.ev.ExtEvSimulation} for an example.
 */
public interface ExtDataSimulation {}
