package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.service.UserService;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Get user details.
   *
   * @param principal Principal context containing information of the user submitting the request
   * @return Created user
   */
  @RequestMapping(method = RequestMethod.POST, path = "/user/me")
  @Secured({})
  public User createUser(Principal principal) {
    log.info("Getting details for user [{}]", principal.getName());
    return userService.get(principal.getName());
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
