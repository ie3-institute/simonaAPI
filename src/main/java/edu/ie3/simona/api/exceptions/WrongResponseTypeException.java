/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.exceptions;

public class WrongResponseTypeException extends RuntimeException {
  public WrongResponseTypeException(String message) {
    super(message);
  }
}
