package com.itworksonmymachine.eduamp.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

  @RequestMapping(method = RequestMethod.GET, path = "/token/list")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN"})
  public List<String> findAllTokens() {
    final Collection<OAuth2AccessToken> tokensByClientId = tokenStore
        .findTokensByClientId(oauthClientId);

    return tokensByClientId.stream().map(token -> token.getValue()).collect(Collectors.toList());
  }
}