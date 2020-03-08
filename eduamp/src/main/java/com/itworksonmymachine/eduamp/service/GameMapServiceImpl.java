package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameMapServiceImpl implements GameMapService {

  private final GameMapRepository gameMapRepository;

  public GameMapServiceImpl(GameMapRepository gameMapRepository) {
    this.gameMapRepository = gameMapRepository;
  }

  @Override
  public GameMap createGameMap(Integer topicId, Integer levelId, GameMap gameMap) {
    return gameMapRepository.save(gameMap);
  }
}
