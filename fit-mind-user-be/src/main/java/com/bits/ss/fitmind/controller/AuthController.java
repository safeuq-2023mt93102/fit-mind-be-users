package com.bits.ss.fitmind.controller;

import com.bits.ss.fitmind.model.ApiException;
import com.bits.ss.fitmind.model.Identity;
import com.bits.ss.fitmind.model.User;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class AuthController {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

  private final Keycloak keycloakClient;
  private final UsersResource usersResource;
  private final UserController userController;

  public AuthController(
      Keycloak keycloakClient, UsersResource usersResource, UserController userController) {
    this.keycloakClient = keycloakClient;
    this.usersResource = usersResource;
    this.userController = userController;
  }

  @PostMapping("/auth/signup")
  public ResponseEntity<?> createIdentity(@RequestBody Identity identity) throws ApiException {
    User givenUser = identity.getUser();
    if (givenUser == null) {
      throw new ApiException.ParamNotSet("user");
    }
    String password = identity.getPassword();
    if (StringUtils.isEmpty(password)) {
      throw new ApiException.ParamNotSet("password");
    }
    User user = userController.createRootUserInternal(givenUser);

    CredentialRepresentation credential = createPasswordCredentials(password);
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(user.getEmail());
    userRepresentation.setFirstName(user.getFirstName());
    userRepresentation.setLastName(user.getLastName());
    userRepresentation.setEmail(user.getEmail());
    userRepresentation.setCredentials(Collections.singletonList(credential));
    userRepresentation.setEnabled(true);
    userRepresentation.singleAttribute("userId", user.getId());

    Response response = usersResource.create(userRepresentation);
    if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
      UserRepresentation userRepresentations =
          usersResource.search(userRepresentation.getEmail()).stream().findFirst().orElseThrow();
      return ResponseEntity.status(response.getStatus()).body(identity);
    }
    return ResponseEntity.status(response.getStatus()).body(response.readEntity(String.class));
  }

  public static CredentialRepresentation createPasswordCredentials(String password) {
    CredentialRepresentation passwordCredentials = new CredentialRepresentation();
    passwordCredentials.setTemporary(false);
    passwordCredentials.setType(CredentialRepresentation.PASSWORD);
    passwordCredentials.setValue(password);
    return passwordCredentials;
  }
}
