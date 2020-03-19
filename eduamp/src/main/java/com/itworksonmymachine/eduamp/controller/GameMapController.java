package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.service.GameMapService;
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
public class GameMapController {

  private final GameMapService gameMapService;

  public GameMapController(GameMapService gameMapService) {
    this.gameMapService = gameMapService;
  }

  /**
   * Fetch all available GameMaps.
   *
   * @param pageable Pagination context
   * @param topicId  Topic id that GameMaps are referenced by
   * @return GameMaps belonging to a specific topic Id
   */
  @RequestMapping(method = RequestMethod.GET, path = "/{topicId}/gameMaps")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<GameMap> fetchAllGameMapByTopicId(Pageable pageable,
      @PathVariable(value = "topicId") Integer topicId) {
    return gameMapService.fetchAllGameMaps(topicId, pageable);
  }

  /**
   * Fetch a specific GameMap.
   *
   * @param topicId   Topic id that GameMap is referenced by
   * @param gameMapId GameMap id that GameMap is referenced by
   * @return GameMap belonging to a specific topic Id and GameMap id
   */
  @RequestMapping(method = RequestMethod.GET, path = "/{topicId}/gameMaps/{gameMapId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public GameMap fetchGameMap(@PathVariable(value = "topicId") Integer topicId,
      @PathVariable(value = "gameMapId") Integer gameMapId) {
    return gameMapService.fetchGameMapById(topicId, gameMapId);
  }

  /**
   * Create a GameMap.
   *
   * @param topicId Topic id that GameMap is referenced by
   * @param gameMap GameMap to be created
   * @return Created GameMap
   */
  @RequestMapping(method = RequestMethod.POST, path = "/{topicId}/gameMaps/create")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public GameMap createGameMap(@PathVariable(value = "topicId") Integer topicId,
      @RequestBody GameMap gameMap) {
    return gameMapService.createGameMap(topicId, gameMap);
  }

  /**
   * Update a GameMap.
   * <p>
   *
   * @param topicId   Topic id that GameMap is referenced by
   * @param gameMapId GameMap id that GameMap is referenced by
   * @param gameMap   GameMap to be updated
   * @param principal Principal context containing information of the user submitting the request
   * @return Updated GameMap
   */
  @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH}, path = "/{topicId}/gameMaps/{gameMapId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public GameMap updateGameMap(@PathVariable(value = "topicId") Integer topicId,
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @RequestBody GameMap gameMap, Principal principal) {
    gameMap.setId(gameMapId);
    return gameMapService.updateGameMap(topicId, gameMap, principal.getName());
  }

  /**
   * Delete a GameMap.
   * <p>
   *
   * @param topicId   Topic id that GameMap is referenced by
   * @param gameMapId GameMap id that GameMap is referenced by
   * @param principal Principal context containing information of the user submitting the request
   * @return Flag indicating if request is successful
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/{topicId}/gameMaps/{gameMapId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public boolean deleteGameMap(@PathVariable(value = "topicId") Integer topicId,
      @PathVariable(value = "gameMapId") Integer gameMapId, Principal principal) {
    return gameMapService.deleteGameMap(topicId, gameMapId, principal.getName());
  }

}