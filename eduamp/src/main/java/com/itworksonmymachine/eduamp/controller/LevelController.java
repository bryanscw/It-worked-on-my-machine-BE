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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/topics"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class LevelController {

  private final LevelService levelService;

  public LevelController(LevelService levelService) {
    this.levelService = levelService;
  }

  /**
   * Fetch all available Levels.
   *
   * @param pageable Pagination context
   * @param topicId  Topic id that level is referenced by
   * @return Levels belonging to a specific topic Id
   */
  @RequestMapping(method = RequestMethod.GET, path = "/{topicId}/levels")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<Level> fetchAllLevelsByTopicId(Pageable pageable,
      @PathVariable(value = "topicId") Integer topicId) {
    return levelService.fetchAllLevels(pageable, topicId);
  }

  /**
   * Fetch a specific level.
   *
   * @param topicId Topic id that level is referenced by
   * @param levelId Level id that level is referenced by
   * @return Levels belonging to a specific topic Id
   */
  @RequestMapping(method = RequestMethod.GET, path = "/{topicId}/levels/{levelId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Level fetchLevel(@PathVariable(value = "topicId") Integer topicId,
      @PathVariable(value = "levelId") Integer levelId) {
    return levelService.fetchLevelById(topicId, levelId);
  }

  /**
   * Create a Level.
   *
   * @param topicId Topic id that level is referenced by
   * @param level   Level to be created
   * @return Created level
   */
  @RequestMapping(method = RequestMethod.POST, path = "/{topicId}/levels/create")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Level createLevel(@PathVariable(value = "topicId") Integer topicId,
      @RequestBody Level level) {
    return levelService.createLevel(topicId, level);
  }

  /**
   * Update a Level.
   * <p>
   * Only the creator of the Level is allowed to modify it.
   *
   * @param topicId   Topic id that level is referenced by
   * @param levelId   Level id that level is referenced by
   * @param level     Level to be updated
   * @param principal Principal context containing information of the user submitting the request
   * @return Updated level
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/{topicId}/levels/{levelId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Level updateLevel(@PathVariable(value = "topicId") Integer topicId,
      @PathVariable(value = "levelId") Integer levelId,
      @RequestBody Level level, Principal principal) {
    level.setId(levelId);
    return levelService.updateLevel(topicId, level, principal.getName());
  }

  /**
   * Delete a Level.
   * <p>
   * Only the creator of the Level is allowed to modify it.
   *
   * @param topicId   Topic id that topic is referenced by
   * @param levelId   Level id that level is referenced by
   * @param principal Principal context containing information of the user submitting the request
   * @return Flag indicating if request is successful
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/{topicId}/levels/{levelId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public boolean deleteTopic(@PathVariable(value = "topicId") Integer topicId,
      @PathVariable(value = "levelId") Integer levelId, Principal principal) {
    return levelService.deleteLevel(topicId, levelId, principal.getName());
  }

}