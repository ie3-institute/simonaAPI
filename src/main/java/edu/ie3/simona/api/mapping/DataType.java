/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.mapping;

import java.util.List;

/** Data types. */
public enum DataType {
  GENERAL,
  PRIMARY,
  RESULT,
  PRIMARY_RESULT,
  EM;

  /** Returns all primary types. */
  public static DataType[] primaryTypes() {
    return new DataType[] {PRIMARY, PRIMARY_RESULT, GENERAL};
  }

  /** Returns all result types. */
  public static DataType[] resultTypes() {
    return new DataType[] {RESULT, PRIMARY_RESULT, GENERAL};
  }

  public static List<DataType> getExceptGeneral() {
      return List.of(PRIMARY, PRIMARY_RESULT, RESULT, EM);
  }

}
