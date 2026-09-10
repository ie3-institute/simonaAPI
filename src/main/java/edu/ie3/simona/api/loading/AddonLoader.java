/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.loading;

import edu.ie3.simona.api.ExtLinkInterface;
import edu.ie3.simona.api.ExtListenerProvider;
import edu.ie3.simona.api.ExtSimulationProvider;
import edu.ie3.simona.api.data.SetupData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Addon loader for SIMONA addons. */
abstract class AddonLoader {

  protected static final Logger log = LoggerFactory.getLogger(AddonLoader.class);

  private final Set<String> allowedExtensions;

  protected AddonLoader(Set<String> allowedExtensions) {
    this.allowedExtensions = allowedExtensions;
  }

  /**
   * Loads the addons at the given path.
   *
   * @param extSimDir the directory containing external simulations
   * @param setupData used for setting up addons
   * @return all provided data
   * @throws IOException - if an I/O error occurs
   */
  public final ProvidedData load(Path extSimDir, SetupData setupData) throws IOException {
    Iterable<File> files = scanDirectory(extSimDir, allowedExtensions);
    ProvidedData allData = ProvidedData.empty();

    for (File file : files) {
      ProvidedData currentData = ProvidedData.empty();

      // loads the addons
      Iterable<ExtLinkInterface> extLinks = load(file);

      for (ExtLinkInterface extLink : extLinks) {
        currentData.add(setUpExtLink(extLink, setupData));
      }

      // some log statement
      log.info("Loaded file '{}' with: {}", file, currentData);

      // add to overall addons
      allData.add(currentData);
    }

    // return all provided data
    return allData;
  }

  /**
   * Method that loads all {@link ExtLinkInterface}s of the given file based on the {@link
   * AddonLoader} used.
   *
   * @param file that contains addons
   * @return all loaded links
   * @throws IOException - if an I/O error occurs
   */
  protected abstract Iterable<ExtLinkInterface> load(File file) throws IOException;

  /**
   * Set up the {@link ExtLinkInterface}s.
   *
   * @param extLink external link interface.
   * @param setupData for setting up the addon
   * @return all loaded data
   */
  private static ProvidedData setUpExtLink(ExtLinkInterface extLink, SetupData setupData) {
    ProvidedData data = ProvidedData.empty();

    // set up the addon
    extLink.setup(setupData);

    // loads the data
    if (extLink instanceof ExtSimulationProvider provider) {
      data.add(provider.getExtSimulation());
    }

    if (extLink instanceof ExtListenerProvider provider) {
      data.add(provider.getResultListeners());
    }

    return data;
  }

  /**
   * Method for building the absolute file path.
   *
   * @param path given path
   * @return the absolute path as a file
   * @throws IOException - if an I/O error occurs
   */
  private static File buildDir(Path path) throws IOException {
    if (path.isAbsolute()) {
      return path.toFile();
    }

    File workingDir = new File(System.getProperty("user.dir"));
    if (!workingDir.isDirectory()) {
      throw new IOException("Error when accessing working directory.");
    }

    return new File(workingDir, path.toString());
  }

  /**
   * Scans the directory for files with allowed extensions.
   *
   * @param extSimDir the directory contains the files.
   * @return all loaded data
   * @throws IOException - if an I/O error occurs
   */
  private static List<File> scanDirectory(Path extSimDir, Set<String> allowedExtensions)
      throws IOException {
    File dir = buildDir(extSimDir);

    if (!dir.isDirectory()) {
      log.warn(
          "External simulation directory {} does not exist or is not a directory, no external simulation loaded.",
          dir.getPath());

      return Collections.emptyList();
    }

    File[] files = dir.listFiles();

    if (files == null) {
      return Collections.emptyList();
    }

    return Arrays.stream(files)
        .filter(
            file -> {
              String name = file.getName();
              return file.canRead()
                  && name.contains(".")
                  && allowedExtensions.contains(
                      name.substring(name.lastIndexOf('.') + 1).toLowerCase());
            })
        .toList();
  }
}
