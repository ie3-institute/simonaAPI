/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.simulation.mapping;

import edu.ie3.datamodel.exceptions.ParsingException;

public enum DataType {
  EXT_PRIMARY_INPUT("primary_input"),
  EXT_EM_INPUT("em_input"),
  EXT_RESULT_GRID("result_grid"),
  EXT_RESULT_PARTICIPANT("result_participant");

  public final String type;

  DataType(String type) {
    this.type = type;
  }

  public static DataType parse(String type) throws ParsingException {
    return switch (type) {
      case "primary_input" -> EXT_PRIMARY_INPUT;
      case "em_input" -> EXT_EM_INPUT;
      case "result_grid" -> EXT_RESULT_GRID;
      case "result_participant" -> EXT_RESULT_PARTICIPANT;
      default -> throw new ParsingException("Data type " + type + " is not supported!");
    };
  }
}
