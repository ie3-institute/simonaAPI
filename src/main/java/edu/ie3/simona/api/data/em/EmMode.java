/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

public enum EmMode {
  BASE("base"),
  EM_COMMUNICATION("emCommunication");

  public final String mode;

  EmMode(String mode) {
    this.mode = mode;
  }
}
