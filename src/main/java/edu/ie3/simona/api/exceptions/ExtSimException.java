/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.exceptions;

/** Exception that is thrown if an error in an external simulation occurred. */
public class ExtSimException extends Exception {

  public ExtSimException(String message) {
    super(message);
  }

  public ExtSimException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public ExtSimException(Throwable throwable) {
    super("Exception thrown in external simulation.", throwable);
  }
}
