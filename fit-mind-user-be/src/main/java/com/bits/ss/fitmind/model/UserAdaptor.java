package com.bits.ss.fitmind.model;

import com.bits.ss.fitmind.database.UserRecord;

import java.util.Objects;

public class UserAdaptor {
  public static UserRecord toUserRecord(User user, String userId, String ownerId) {
    return new UserRecord(userId, ownerId, user.getFirstName(), user.getLastName(), user.getEmail(), user.getDateOfBirth());
  }

  public static UserRecord toUserRecord(User user, UserRecord oldUser, String ownerId) {
    return new UserRecord(
        Objects.requireNonNullElse(user.getId(), oldUser.getId()),
        Objects.requireNonNullElse(ownerId, oldUser.getOwnerId()),
        Objects.requireNonNullElse(user.getFirstName(), oldUser.getFirstName()),
        Objects.requireNonNullElse(user.getLastName(), oldUser.getLastName()),
        Objects.requireNonNullElse(user.getEmail(), oldUser.getEmail()),
        Objects.requireNonNullElse(user.getDateOfBirth(), oldUser.getDateOfBirth()));
  }
}
