/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.models.Entity;
import java.util.Optional;

/** Interface definition of a source, that is able to provide external mapping. */
public abstract class ExtEntityMappingSource {

  protected final ExtEntityFactory factory;
  protected static final EntityPersistenceNamingStrategy namingStrategy = new ExtEntityNaming();

  protected ExtEntityMappingSource() {
    this.factory = new ExtEntityFactory();
  }

  private static class ExtEntityNaming extends EntityPersistenceNamingStrategy {
    @Override
    public Optional<String> getEntityName(Class<? extends Entity> cls) {
      if (ExtEntityEntry.class.isAssignableFrom(cls)) {
        return Optional.of("ext_entity_mapping");
      } else {
        return super.getEntityName(cls);
      }
    }
  }
}
