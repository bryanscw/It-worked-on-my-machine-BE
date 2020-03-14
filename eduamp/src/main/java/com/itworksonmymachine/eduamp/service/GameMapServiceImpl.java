package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.exception.ResourceNotFoundException;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameMapServiceImpl implements GameMapService {

  private final GameMapRepository gameMapRepository;

  private final TopicRepository topicRepository;

  public GameMapServiceImpl(GameMapRepository gameMapRepository, TopicRepository topicRepository) {
    this.gameMapRepository = gameMapRepository;
    this.topicRepository = topicRepository;
  }

  @Override
  public Page<GameMap> fetchAllGameMaps(Integer topicId, Pageable pageable) {
    return gameMapRepository.findGameMapsByTopic_Id(topicId, pageable);
  }

  @Override
  public GameMap fetchGameMapById(Integer topicId, Integer gameMapId) {
    String errorMsg = String
        .format("Level with topicId [%s] and gameMapId [%s] not found", topicId, gameMapId);
    GameMap gameMap = gameMapRepository.findById(gameMapId)
        .orElseThrow(() -> new ResourceNotFoundException(errorMsg));

    // Sanity check: Check if the topicId is the parentId of gameMap
    if (gameMap.getTopic().getId() != topicId) {
      throw new ResourceNotFoundException(errorMsg);
    }
    return gameMap;
  }

  @Override
  public GameMap createGameMap(Integer topicId, GameMap gameMap) {
    Topic topicToFind = topicRepository.findById(topicId).orElseThrow(() -> {
      String errorMsg = String.format("Topic with topicId: [%s] not found", topicId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });
    gameMap.setTopic(topicToFind);
    return gameMapRepository.save(gameMap);
  }

  @Override
  public GameMap updateGameMap(Integer topicId, GameMap gameMap, String userEmail) {
    GameMap gameMapToFind = gameMapRepository.findById(gameMap.getId()).orElseThrow(() -> {
      String errorMsg = String.format("GameMap with gameMapId: [%s] not found", gameMap.getId());
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    if (gameMapToFind.getTopic().getId() != topicId) {
      String errorMsg = String
          .format("GameMap with topicId [%s] and gameMap: [%s] not found", topicId,
              gameMap.getId());
      log.error(errorMsg);
      throw new ResourceNotFoundException(errorMsg);
    }

    if (gameMap.getMapDescriptor() != null) {
      gameMapToFind.setMapDescriptor(gameMap.getMapDescriptor());
    }

    if (gameMap.getQuestions() != null || !gameMap.getQuestions().isEmpty()) {
      gameMapToFind.setQuestions(gameMap.getQuestions());
    }

    gameMapToFind.setPlayable(gameMap.isPlayable());

//    // Only the creator/owner of the level is allowed to modify it
//    if (!gameMapToFind.getCreatedBy().equals(userEmail)) {
//      throw new NotAuthorizedException();
//    }

    return gameMapRepository.save(gameMap);
  }

  @Override
  public boolean deleteGameMap(Integer topicId, Integer gameMapId, String userEmail) {
    GameMap gameMapToFind = gameMapRepository.findById(gameMapId).orElseThrow(() -> {
      String errorMsg = String.format("GameMap with gameMapId: [%s] not found", gameMapId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    if (gameMapToFind.getTopic().getId() != topicId) {
      String errorMsg = String
          .format("GameMap with topicId [%s] and gameMapId: [%s] not found", topicId, gameMapId);
      log.error(errorMsg);
      throw new ResourceNotFoundException(errorMsg);
    }

//    // Only the creator/owner of the level is allowed to modify it
//    if (!gameMapToFind.getCreatedBy().equals(userEmail)) {
//      throw new NotAuthorizedException();
//    }

    // Delete the GameMap
    gameMapRepository.delete(gameMapToFind);

    return true;
  }
}
