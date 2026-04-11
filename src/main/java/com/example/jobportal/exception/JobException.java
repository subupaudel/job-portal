package com.example.jobportal.exception;

import org.springframework.http.HttpStatus;

public class JobException extends RuntimeException {
  private final HttpStatus status;

  public JobException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }

  // Convenience static methods for common cases
  public static JobException notFound(String message) {
    return new JobException(message, HttpStatus.NOT_FOUND);
  }

  public static JobException badRequest(String message) {
    return new JobException(message, HttpStatus.BAD_REQUEST);
  }

  public static JobException forbidden(String message) {
    return new JobException(message, HttpStatus.FORBIDDEN);
  }

  public static JobException unauthorized(String message) {
    return new JobException(message, HttpStatus.UNAUTHORIZED);
  }

  public static JobException internalServerError(String message) {
    return new JobException(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}