package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.Progress;
import com.itworksonmymachine.eduamp.model.dto.LeaderboardResultDTO;
import com.itworksonmymachine.eduamp.model.dto.QuestionAttemptDTO;
import com.itworksonmymachine.eduamp.service.ProgressService;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
      @PathVariable(value = "gameMapId") Integer gameMapId,
      Authentication authentication
  ) {
    log.info("Fetching progress for User with email: [{}] and GameMap with gameMapId: [{}]",
        userEmail, gameMapId);
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
  public Page<Progress> fetchProgressByGameMapId(
      Pageable pageable,
      @PathVariable(value = "gameMapId") Integer gameMapId,
      Authentication authentication
  ) {
    log.info("Fetching progress for GameMap with gameMapId: [{}]", gameMapId);
    return progressService.fetchAllProgressByGameMapId(gameMapId, authentication, pageable);
  }

  /**
   * Fetch and rank players of a certain GameMap by their performance.
   *
   * @param gameMapId GameMap id
   * @return LeaderBoardDTO containing the players and their time taken of the specific GameMap
   */
  @RequestMapping(method = RequestMethod.GET, path = "/gameMaps/{gameMapId}/leaderboard")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public ArrayList<LeaderboardResultDTO> getLeaderboard(
      @PathVariable(value = "gameMapId") Integer gameMapId
  ) {
    log.info("Fetching leaderboard for GameMap with gameMapId: [{}]", gameMapId);
    return progressService.fetchLeaderboardByGameMapId(gameMapId);
  }

  /**
   * Fetch the attempt count of all students that attempted of a certain GameMap.
   *
   * @param gameMapId GameMap id
   * @return QuestionAttemptDTO containing the attempt count of the specific GameMap
   */
  @RequestMapping(method = RequestMethod.GET, path = "/gameMaps/{gameMapId}/report")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public ArrayList<QuestionAttemptDTO> getAttemptCountByGameMapId(
      @PathVariable(value = "gameMapId") Integer gameMapId
  ) {
    log.info("Fetching report for GameMap with gameMapId: [{}]", gameMapId);
    return progressService.fetchAttemptCountByGameMapId(gameMapId);
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
  public Page<Progress> fetchProgressByUserEmail(
      Pageable pageable,
      @PathVariable(value = "userEmail") String userEmail,
      Authentication authentication
  ) {
    log.info("Fetching progress of user with email: [{}]", userEmail);
    return progressService.fetchAllProgressByUserEmail(userEmail, authentication, pageable);
  }

  /**
   * Create a Progress of a User in a specific GameMap.
   *
   * @param userEmail      Email of User
   * @param gameMapId      GameMap id
   * @param progress       Progress to be created
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Created Progress
   */
  @RequestMapping(method = RequestMethod.POST, path = "/users/{userEmail}/gameMaps/{gameMapId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Progress createProgress(
      @PathVariable(value = "userEmail") String userEmail,
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @RequestBody Progress progress,
      Authentication authentication
  ) {
    log.info("Creating progress for User with email: [{}] and GameMap with gameMapId: [{}]",
        userEmail, gameMapId);
    return progressService.createProgress(userEmail, gameMapId, progress, authentication);
  }

  /**
   * Delete a Progress of a User in a specific GameMap.
   *
   * @param userEmail      Email of User
   * @param gameMapId      GameMap id
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Deleted flag; True of the deletion is successful
   */
  @RequestMapping(method = RequestMethod.POST, path = "/users/{userEmail}/gameMaps/{gameMapId}/progress/{progressId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public boolean deleteProgress(
      @PathVariable(value = "userEmail") String userEmail,
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @PathVariable(value = "progressId") Integer progressId,
      Authentication authentication
  ) {
    log.info("Creating progress for User with email: [{}] and GameMap with gameMapId: [{}]",
        userEmail, gameMapId);
    return progressService.deleteProgress(userEmail, gameMapId, progressId, authentication);
  }


  /**
   * Process user answer submission
   *
   * @param userEmail      Email of User
   * @param gameMapId      GameMap id
   * @param questionId     Question id
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Created Progress
   */
  @RequestMapping(method = RequestMethod.POST, path = "/users/{userEmail}/gameMaps/{gameMapId}/questions/{questionId}/submit")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_STUDENT"})
  public boolean submitAnswer(
      @PathVariable(value = "userEmail") String userEmail,
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @PathVariable(value = "questionId") Integer questionId,
      @RequestBody Integer answer,
      Authentication authentication
  ) {
    log.info(
        "Checking answer: [{}] submitted by User with email: [{}] and GameMap with gameMapId: [{}] for question with questionId: [{}]",
        answer, userEmail, gameMapId, questionId);
    return progressService.checkAnswer(userEmail, gameMapId, questionId, answer, authentication);
  }


  /**
   * Update a Progress of a user in a specific GameMap.
   *
   * @param userEmail      Email of User
   * @param gameMapId      GameMap id
   * @param progress       Progress to be created
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Updated Progress
   */
  @RequestMapping(method = {RequestMethod.PUT,
      RequestMethod.PATCH}, path = "/users/{userEmail}/gameMaps/{gameMapId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Progress updateProgress(
      @PathVariable(value = "userEmail") String userEmail,
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @RequestBody Progress progress,
      Authentication authentication
  ) {
    log.info("Updating progress for User with email: [{}] and GameMap with gameMapId: [{}]",
        userEmail, gameMapId);
    return progressService.updateProgress(userEmail, gameMapId, progress, authentication);
  }

}