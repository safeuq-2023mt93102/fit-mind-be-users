package com.bits.ss.fitmind.model;

import com.bits.ss.fitmind.model.ApiException.ParamPatternInvalid;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.StringJoiner;

public final class User {
  public static final String DATE_PATTERN = "dd-MM-yyyy";
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

  private final String id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final LocalDate dateOfBirth;

  public User(String id, String firstName, String lastName, String email, LocalDate dateOfBirth) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.dateOfBirth = dateOfBirth;
  }

  @JsonCreator
  public static User of(
      @JsonProperty("id") String id,
      @JsonProperty("first_name") String firstName,
      @JsonProperty("last_name") String lastName,
      @JsonProperty("email") String email,
      @JsonProperty("date_of_birth") String dateOfBirth)
      throws ParamPatternInvalid {
    LocalDate dateOfBirthParsed = null;
    try {
      if (StringUtils.isNotEmpty(dateOfBirth)) {
        dateOfBirthParsed = LocalDate.parse(dateOfBirth, DATE_FORMATTER);
      }
    } catch (DateTimeParseException exception) {
      throw new ParamPatternInvalid("date_of_birth");
    }
    return new User(id, firstName, lastName, email, dateOfBirthParsed);
  }

  public User withEmail(String email) {
    return new User(id, firstName, lastName, email, dateOfBirth);
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("first_name")
  public String getFirstName() {
    return firstName;
  }

  @JsonProperty("last_name")
  public String getLastName() {
    return lastName;
  }

  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  @JsonIgnore
  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  @JsonProperty("date_of_birth")
  public String getDobString() {
    return DATE_FORMATTER.format(dateOfBirth);
  }

  @Override
  public String toString() {
    StringJoiner result = new StringJoiner(", ", "{", "}");
    if (id != null) {
      result.add("\"id\": " + "\"" + id + "\"");
    }
    if (firstName != null) {
      result.add("\"first_name\": " + "\"" + firstName + "\"");
    }
    if (lastName != null) {
      result.add("\"last_name\": " + "\"" + lastName + "\"");
    }
    if (email != null) {
      result.add("\"email\": " + "\"" + email + "\"");
    }
    if (dateOfBirth != null) {
      result.add("\"date_of_birth\": " + "\"" + getDobString() + "\"");
    }
    return result.toString();
  }
}
