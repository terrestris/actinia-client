package de.terrestris.actinia;

public class ActiniaException extends RuntimeException {

  public ActiniaException(String message, Exception cause) {
    super(message, cause);
  }

  public ActiniaException(String message) {
    super(message);
  }

}
