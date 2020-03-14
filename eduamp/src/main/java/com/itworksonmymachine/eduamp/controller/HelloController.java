package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.model.Welcome;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = {"/hello"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class HelloController {

  /**
   * This is an example controller method to test for authorization and principal identification.
   *
   * @param name      Sample input string passed in as a request parameter
   * @param principal Principal context containing information of the user submitting the request
   * @return JSON string with the request parameter and id (email) of user submitting the request
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Welcome greetings(@RequestParam("name") String name, Principal principal) {
    return new Welcome(name + " (" + principal.getName() + ")");
  }

}
