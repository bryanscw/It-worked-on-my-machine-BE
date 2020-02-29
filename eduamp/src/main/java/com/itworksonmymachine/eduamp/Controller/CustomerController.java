package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.model.Response;
import com.itworksonmymachine.eduamp.model.UserTokenSession;
import com.itworksonmymachine.eduamp.service.UserTokenSessionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/customer")
@Api(value = "Customer API", tags = "Customer")
public class CustomerController {

  @Value("${config.oauth2.tokenTimeout}")
  private long tokenExpiryTime;

  @Autowired
  private UserTokenSessionServiceImpl userTokenSessionService;


  @PostMapping(path = "register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  @ApiOperation(value = "Register a customer")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success", response = UserTokenSession.class),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden"),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 500, message = "Failure")})
  public Response registerCustomer(@RequestHeader HttpHeaders httpHeaders, Principal principal,
      @RequestParam("name") String name, @RequestParam("email") String email,
      @RequestParam("password") String password) {

    String username = principal.getName();
    UserTokenSession userTokenSession = userTokenSessionService
        .buildUserTokenSession(principal, httpHeaders);
    userTokenSession = userTokenSessionService.saveUserTokenSessionMapping(userTokenSession);

    log.info(
        "User [{}] successfully logged in. User, Token and Session mapping stored. [{}]", username,
        userTokenSession);

    return Response.of(true, 200, "Customer Updated");
  }


}
