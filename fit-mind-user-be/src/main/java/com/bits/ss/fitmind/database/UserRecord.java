package com.bits.ss.fitmind.database;

import com.bits.ss.fitmind.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@IdClass(UserRecord.PrimaryKey.class)
public final class UserRecord {
  @Id private String id;
  private String ownerId;

  private String firstName;
  private String lastName;

  @Column(unique = true, nullable = false)
  private String email;

  private LocalDate dateOfBirth;

  public User toUser() {
    return new User(id, firstName, lastName, email, dateOfBirth);
  }

  public static class PrimaryKey implements Serializable {
    private String id;
    private String email;
  }
}
