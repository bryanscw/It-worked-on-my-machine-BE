package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.Level;
import com.itworksonmymachine.eduamp.service.LevelService;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/level"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class LevelController {

  private final LevelService levelService;

  public LevelController(LevelService levelService) {
    this.levelService = levelService;
  }

  /**
   * Fetch all available levels.
   *
   * @param pageable
   * @param topicId  Topic id that level is referenced by
   * @return Levels belonging to a specific topic Id
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<Level> fetchAllLevels(Pageable pageable, Integer topicId) {
    return levelService.fetchAllLevels(pageable, topicId);
  }

  /**
   * Create a Level.
   *
   * @param level Level to be created
   * @return Created level
   */
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Level createLevel(Level level) {
    return levelService.createLevel(level);
  }

  /**
   * Update a Level. Only the creator of the level is allowed to modify it.
   *
   * @param level     Level to be updated
   * @param principal
   * @return Updated level
   */
  @RequestMapping(method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Level updateLevel(Level level, Principal principal) {
    return levelService.updateLevel(level, principal.getName());
  }

}