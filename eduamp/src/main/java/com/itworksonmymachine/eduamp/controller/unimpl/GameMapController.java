package com.itworksonmymachine.eduamp.controller.unimpl;

import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.service.GameMapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
   * Create a GameMap.
   *
   * @param gameMap GameMap to be created
   * @return Created GameMap
   */
  @RequestMapping(method = RequestMethod.POST, path = "/{topicId}/levels/{levelId}/create")
  @Secured({"ROLE_TEACHER"})
  public GameMap createGameMap(@PathVariable(value = "topicId") Integer topicId,
      @PathVariable(value = "levelId") Integer levelId, GameMap gameMap) {
    return gameMapService.createGameMap(topicId, levelId, gameMap);
  }

}