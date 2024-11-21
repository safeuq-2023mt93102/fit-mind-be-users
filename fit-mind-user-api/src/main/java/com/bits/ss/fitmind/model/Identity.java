package com.bits.ss.fitmind.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Identity {
  private final String password;
  private final User user;

  private Identity(String password, User user) {
    this.password = password;
    this.user = user;
  }

  @JsonCreator
  public static Identity of(
      @JsonProperty("password") String password, @JsonProperty("user") User user) {
    return new Identity(password, user);
  }

  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  @JsonIgnore
  public User getUser() {
    return user;
  }
}
