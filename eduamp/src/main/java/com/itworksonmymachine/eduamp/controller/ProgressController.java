package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.Progress;
import com.itworksonmymachine.eduamp.service.ProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/progress"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class ProgressController {

  private final ProgressService progressService;

  public ProgressController(ProgressService progressService) {
    this.progressService = progressService;
  }

  /**
   * Fetch a Progress belonging to a User of a certain GameMap.
   *
   * @param userEmail      User email
   * @param gameMapId      GameMap id
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Progress belonging of a User referenced by User email of a GameMap referenced by
   * gameMapId
   */
  @RequestMapping(method = RequestMethod.GET, path = "/users/{userEmail}/gameMaps/{gameMapId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Progress fetchProgressByUserEmailAndGameMapId(
      @PathVariable(value = "userEmail") String userEmail,
      @PathVariable(value = "gameMapId") Integer gameMapId, Authentication authentication) {
    return progressService
        .fetchProgressByUserEmailAndGameMapId(userEmail, gameMapId, authentication);
  }

  /**
   * Fetch all Progress of Users of a certain GameMap.
   *
   * @param pageable       Pagination context
   * @param gameMapId      GameMap id
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Progress of a certain GameMap referenced by gameMapId
   */
  @RequestMapping(method = RequestMethod.GET, path = "/gameMaps/{gameMapId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<Progress> fetchProgressByGameMapId(Pageable pageable,
      @PathVariable(value = "gameMapId") Integer gameMapId, Authentication authentication) {
    return progressService.fetchAllProgressByGameMapId(gameMapId, authentication, pageable);
  }

  /**
   * Fetch all Progress of a certain user.
   *
   * @param pageable       Pagination context
   * @param userEmail      Email of User
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Progress of a User referenced by User email.
   */
  @RequestMapping(method = RequestMethod.GET, path = "/users/{userEmail}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<Progress> fetchProgressByUserEmail(Pageable pageable,
      @PathVariable(value = "userEmail") String userEmail, Authentication authentication) {
    return progressService.fetchAllProgressByUserEmail(userEmail, authentication, pageable);
  }

}