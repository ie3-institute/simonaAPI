/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

/** Interface definition of a source, that is able to provide external mapping. */
public abstract class ExtEntityMappingSource {

  protected final ExtEntityFactory factory;

  public ExtEntityMappingSource() {
    this.factory = new ExtEntityFactory();
  }
}
