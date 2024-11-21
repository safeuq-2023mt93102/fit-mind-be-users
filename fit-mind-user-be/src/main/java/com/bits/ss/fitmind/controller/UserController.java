package com.bits.ss.fitmind.controller;

import com.bits.ss.fitmind.database.UserRecord;
import com.bits.ss.fitmind.model.ApiException.ParamNotEditable;
import com.bits.ss.fitmind.model.ApiException.ParamNotSet;
import com.bits.ss.fitmind.model.ApiException.ParamNotUnique;
import com.bits.ss.fitmind.model.User;
import com.bits.ss.fitmind.model.UserAdaptor;
import com.bits.ss.fitmind.repository.UserRepository;
import com.bits.ss.fitmind.security.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createRootUserInternal(User user)
      throws ParamNotSet, ParamNotUnique, ParamNotEditable {
    String rootUserId = "user-" + UUID.randomUUID();
    return createUserInternal(user, rootUserId, rootUserId);
  }

  public User createUserInternal(User user, String userId, String ownerId)
      throws ParamNotSet, ParamNotUnique, ParamNotEditable {
    if (StringUtils.isEmpty(user.getFirstName())) {
      throw new ParamNotSet("first_name");
    }
    if (StringUtils.isEmpty(user.getLastName())) {
      throw new ParamNotSet("last_name");
    }
    if (StringUtils.isEmpty(user.getEmail())) {
      throw new ParamNotSet("email");
    }
    if (StringUtils.isNotEmpty(user.getId())) {
      throw new ParamNotEditable("id");
    }
    try {
      UserRecord savedUser = userRepository.save(UserAdaptor.toUserRecord(user, userId, ownerId));
      return savedUser.toUser();
    } catch (DataIntegrityViolationException exception) {
      throw new ParamNotUnique("email");
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable("id") String userId, Principal principal) {
    return userRepository
        .findByIdAndOwnerId(userId, SecurityUtil.getOwnerId(principal))
        .map(userRecord -> ResponseEntity.ok(userRecord.toUser()))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/")
  public ResponseEntity<List<User>> getUsers(Principal principal) {
    return ResponseEntity.ok(
        userRepository.findAllByOwnerId(SecurityUtil.getOwnerId(principal)).stream()
            .map(UserRecord::toUser)
            .collect(Collectors.toList()));
  }

  @PostMapping("/{id}")
  public ResponseEntity<?> updateUser(
      @PathVariable String id, @RequestBody User user, Principal principal) {
    Optional<UserRecord> oldUserOptional =
        userRepository.findByIdAndOwnerId(id, SecurityUtil.getOwnerId(principal));
    if (oldUserOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    UserRecord oldUser = oldUserOptional.get();
    if (StringUtils.isNotEmpty(user.getEmail())
        && !Objects.equals(user.getEmail(), oldUser.getEmail())) {
      return ResponseEntity.badRequest().body(new ParamNotEditable("email"));
    }
    try {
      UserRecord savedUser = userRepository.save(UserAdaptor.toUserRecord(user, oldUser, null));
      return ResponseEntity.ok().body(savedUser);
    } catch (DataIntegrityViolationException exception) {
      return ResponseEntity.badRequest().body(new ParamNotUnique("email"));
    }
  }
}
