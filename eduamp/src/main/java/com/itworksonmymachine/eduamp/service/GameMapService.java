package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.GameMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameMapService {

  Page<GameMap> fetchAllGameMaps(Integer topicId, Pageable pageable);

  GameMap fetchGameMapById(Integer topicId, Integer gameMapId);

  GameMap createGameMap(Integer topicId, GameMap gameMap);

  GameMap updateGameMap(Integer topicId, GameMap gameMap, String userEmail);

  boolean deleteGameMap(Integer topicId, Integer gameMapId, String userEmail);

}
