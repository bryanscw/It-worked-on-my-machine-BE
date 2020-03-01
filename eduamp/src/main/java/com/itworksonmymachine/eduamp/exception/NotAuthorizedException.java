package com.itworksonmymachine.eduamp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown when user is not authorized.
 * <p>
 * A HTTP 401 status code will be thrown.
 *
 * @author suvoonhou
 */

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAuthorizedException extends RuntimeException {

  public NotAuthorizedException() {
    super();
  }

  public NotAuthorizedException(String message) {
    super(message);
  }

  public NotAuthorizedException(String message, Throwable cause) {
    super(message, cause);
  }
}

