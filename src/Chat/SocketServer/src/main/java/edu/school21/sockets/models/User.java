package edu.school21.sockets.models;

public class User {
  private Long id;
  private String email;
  private String password;

  public User(Long id, String email, String password) {
    this.email = email;
    this.id = id;
    this.password = password;
  }

  public long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
