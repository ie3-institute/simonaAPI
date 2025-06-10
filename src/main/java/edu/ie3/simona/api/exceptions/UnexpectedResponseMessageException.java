/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.exceptions;

/** This exception is thrown, if an unexpected response message type was received. */
public class UnexpectedResponseMessageException extends RuntimeException {
  public UnexpectedResponseMessageException(String message) {
    super(message);
  }
}
