/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.loading;

import edu.ie3.simona.api.ExtLinkInterface;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;
import java.util.Set;

/** Implementation of {@link AddonLoader} for jars. */
public final class JarLoader extends AddonLoader {

  public JarLoader() {
    super(Set.of("jar"));
  }

  @Override
  protected Iterable<ExtLinkInterface> load(File file) throws IOException {
    try (URLClassLoader classLoader = new URLClassLoader(new URL[] {file.toURI().toURL()})) {
      return ServiceLoader.load(ExtLinkInterface.class, classLoader);

    } catch (Exception e) {
      throw new IOException(e);
    }
  }
}
