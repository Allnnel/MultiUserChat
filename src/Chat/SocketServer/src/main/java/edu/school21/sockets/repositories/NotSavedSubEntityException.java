package edu.school21.sockets.repositories;

public class NotSavedSubEntityException extends RuntimeException {
  public NotSavedSubEntityException() {
    super("No data available.");
  }
}
