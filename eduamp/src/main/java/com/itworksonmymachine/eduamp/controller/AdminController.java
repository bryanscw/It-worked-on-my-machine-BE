package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.service.UserService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/admin"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class AdminController {

  private final TokenStore tokenStore;
  private final UserService userService;
  @Value("${oauth2.client-id}")
  private String oauthClientId;

  public AdminController(TokenStore tokenStore, UserService userService) {
    this.tokenStore = tokenStore;
    this.userService = userService;
  }

  /**
   * List all valid tokens in the token store.
   *
   * @return All tokens in the token store.
   */
  @RequestMapping(method = RequestMethod.GET, path = "/token/list")
  @Secured({"ROLE_ADMIN"})
  public List<String> findAllTokens() {
    final Collection<OAuth2AccessToken> tokensByClientId = tokenStore
        .findTokensByClientId(oauthClientId);
    return tokensByClientId.stream().map(OAuth2AccessToken::getValue).collect(Collectors.toList());
  }

  /**
   * Create a new user.
   *
   * @param user User to be created
   * @return Created user
   */
  @RequestMapping(method = RequestMethod.POST, path = "/user/create")
  @Secured({"ROLE_ADMIN"})
  public User createUser(@RequestBody User user) {
    log.info("Creating user [{}] with role [{}]", user.getEmail(), user.getRole());
    return userService.create(user);
  }

}