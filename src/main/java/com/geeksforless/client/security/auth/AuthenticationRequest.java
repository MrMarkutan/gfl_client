package com.geeksforless.client.security.auth;


import java.util.Objects;

public class AuthenticationRequest {
  private String login;
  private String password;

  public AuthenticationRequest(String login, String password) {
    this.login = login;
    this.password = password;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthenticationRequest that = (AuthenticationRequest) o;
    return Objects.equals(login, that.login) && Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(login, password);
  }
}
