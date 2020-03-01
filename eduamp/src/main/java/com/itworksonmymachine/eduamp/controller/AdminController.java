package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.service.UserService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Value("${oauth2.client-id}")
  private String oauthClientId;

  @Autowired
  private TokenStore tokenStore;

  @Autowired
  private UserService userService;

  @RequestMapping(method = RequestMethod.GET, path = "/token/list")
  @Secured({"ROLE_ADMIN"})
  public List<String> findAllTokens() {
    final Collection<OAuth2AccessToken> tokensByClientId = tokenStore
        .findTokensByClientId(oauthClientId);
    return tokensByClientId.stream().map(token -> token.getValue()).collect(Collectors.toList());
  }

  @RequestMapping(method = RequestMethod.POST, path = "/user/create")
  @Secured({"ROLE_ADMIN"})
  public User createUser(@RequestBody User user) {
    log.info("Creating user [{}] with role [{}]", user.getEmail(), user.getRole());
    return userService.create(user);
  }

}