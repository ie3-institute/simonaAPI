/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data;

import com.typesafe.config.Config;
import edu.ie3.datamodel.models.input.container.GridContainer;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.pekko.actor.typed.scaladsl.ActorContext;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * Container class that holds all data needed for setting up an external simulation.
 *
 * @param mainArgs CLI arguments with which SIMONA is initiated.
 * @param config The parsed simona config.
 * @param context Actor context of the simona simulation.
 * @param gridContainer The electrical grid.
 * @param baseInputDirectory The base input directory.
 * @param baseOutputDirectory The base output directory.
 */
public record SetupData(
    String[] mainArgs,
    Config config,
    ActorContext<?> context,
    GridContainer gridContainer,
    Path baseInputDirectory,
    Path baseOutputDirectory) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    SetupData setupData = (SetupData) o;
    return Arrays.equals(mainArgs, setupData.mainArgs)
        && Objects.equals(config, setupData.config) 
        && Objects.equals(context, setupData.context)
        && Objects.equals(gridContainer, setupData.gridContainer)
        && Objects.equals(baseInputDirectory, setupData.baseInputDirectory)
        && Objects.equals(baseOutputDirectory, setupData.baseOutputDirectory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        Arrays.hashCode(mainArgs), config, context, gridContainer, baseInputDirectory, baseOutputDirectory);
  }

  @Override
  @NonNull
  public String toString() {
    return "SetupData{"
        + "mainArgs="
        + Arrays.toString(mainArgs)
        + ", config="
        + config
            + ", context="
            + context
        + ", gridContainer="
        + gridContainer
        + ", baseInputDirectory="
        + baseInputDirectory
        + ", baseOutputDirectory="
        + baseOutputDirectory
        + '}';
  }
}
