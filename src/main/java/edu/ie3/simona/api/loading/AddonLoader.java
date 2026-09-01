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
public abstract sealed class AddonLoader permits JarLoader {

  protected static final Logger log = LoggerFactory.getLogger(AddonLoader.class);

  private final Set<String> allowedExtensions;

  protected AddonLoader(Set<String> allowedExtensions) {
    this.allowedExtensions = allowedExtensions;
  }

  /**
   * Method that uses all registered {@link AddonLoader} to load all available addons in the given
   * directory.
   *
   * @param extSimDir directory containing files with addons
   * @param setupData used for setting up addons
   * @return all provided data
   * @throws IOException - if an I/O error occurs
   */
  public static ProvidedData load(Path extSimDir, SetupData setupData) throws IOException {
    ProvidedData data = ProvidedData.empty();

    // all registered loaders
    List<AddonLoader> loaders = List.of(new JarLoader());

    for (AddonLoader loader : loaders) {
      data.add(loader.loadAddons(extSimDir, setupData));
    }

    return data;
  }

  /**
   * Loads the addons at the given path.
   *
   * @param extSimDir the directory containing external simulations
   * @param setupData used for setting up addons
   * @return all provided data
   * @throws IOException - if an I/O error occurs
   */
  private ProvidedData loadAddons(Path extSimDir, SetupData setupData) throws IOException {
    Iterable<File> files = scanDirectory(extSimDir);
    ProvidedData data = ProvidedData.empty();

    for (File file : files) {
      // loads and sets up the addons
      ProvidedData current = setUpExtLinks(file, setupData);

      // add to overall addons
      data.add(current);
    }

    // return all provided data
    return data;
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
   * Sets up all {@link ExtLinkInterface}s.
   *
   * @param file the file with the external links.
   * @param setupData for setting up the addon
   * @return all loaded data
   * @throws IOException - if an I/O error occurs
   */
  private ProvidedData setUpExtLinks(File file, SetupData setupData) throws IOException {
    Iterable<ExtLinkInterface> extLinks = load(file);

    ProvidedData data = ProvidedData.empty();

    for (ExtLinkInterface extLink : extLinks) {
      // set up the addon
      extLink.setup(setupData);

      // loads the data
      if (extLink instanceof ExtSimulationProvider provider) {
        data.add(provider.getExtSimulation());
      }

      if (extLink instanceof ExtListenerProvider provider) {
        data.add(provider.getResultListeners());
      }
    }

    // some log statement
    log.info("Loaded file '{}' with: {}", file, data);

    return data;
  }

  /**
   * Method for building the absolute file path.
   *
   * @param path given path
   * @return the absolute path as a file
   * @throws IOException - if an I/O error occurs
   */
  private File buildDir(Path path) throws IOException {
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
  private Iterable<File> scanDirectory(Path extSimDir) throws IOException {
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
