/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.loading

import edu.ie3.simona.api.ExtLinkInterface

import java.io.File
import java.lang.Iterable
import java.net.{URL, URLClassLoader}
import java.util
import java.util.ServiceLoader

private final class JarLoader extends AddonLoader(util.Set.of("jar")) {

  override protected def load(file: File): Iterable[ExtLinkInterface] = {
    val classLoader = new URLClassLoader(Array[URL](file.toURI.toURL))
    ServiceLoader.load(classOf[ExtLinkInterface], classLoader)
  }

}
