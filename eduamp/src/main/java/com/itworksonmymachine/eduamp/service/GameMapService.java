package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.GameMap;

public interface GameMapService {

  GameMap createGameMap(Integer topicId, Integer levelId, GameMap gameMap);

}
