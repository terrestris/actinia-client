package de.terrestris.actinia;

/**
 * This exception will be thrown if anything goes wrong when communicating with actinia.
 */
public class ActiniaException extends RuntimeException {

  public ActiniaException(String message, Exception cause) {
    super(message, cause);
  }

  public ActiniaException(String message) {
    super(message);
  }

}
