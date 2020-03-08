package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Level;
import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.exception.ResourceNotFoundException;
import com.itworksonmymachine.eduamp.repository.LevelRepository;
import com.itworksonmymachine.eduamp.repository.TopicRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class LevelServiceImpl implements LevelService {

  private final LevelRepository levelRepository;

  private final TopicRepository topicRepository;

  public LevelServiceImpl(LevelRepository levelRepository, TopicRepository topicRepository) {
    this.levelRepository = levelRepository;
    this.topicRepository = topicRepository;
  }

  @Override
  public Page<Level> fetchAllLevels(Pageable pageable, Integer topicId) {
    return levelRepository.findLevelsByTopic_Id(pageable, topicId);
  }

  @Override
  public Level fetchLevelById(Integer topicId, Integer levelId) {
    // Although topicId is not allowed, it makes brute forcing a little harder
    String errorMsg = String
        .format("Level with topicId [%s] and levelId [%s] not found", topicId, levelId);
    Level level = levelRepository.findById(levelId).orElseThrow(() -> new ResourceNotFoundException(
        errorMsg));

    // Sanity check: Check if the topicId is the parentId of level
    if (level.getTopic().getId() != topicId) {
      throw new ResourceNotFoundException(errorMsg);
    }

    return level;
  }

  @Override
  public Level createLevel(Integer topicId, Level level) {
    Topic topicToFind = topicRepository.findById(topicId).orElseThrow(() -> {
      String errorMsg = String.format("Topic with topicId: [%s] not found", topicId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });
    level.setTopic(topicToFind);
    return levelRepository.save(level);
  }

  @Override
  public Level updateLevel(Integer topicId, Level level, String userEmail) {
    Level levelToFind = levelRepository.findById(level.getId()).orElseThrow(() -> {
      String errorMsg = String.format("Level with levelId: [%s] not found", level.getId());
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    if (levelToFind.getTopic().getId() != topicId) {
      String errorMsg = String
          .format("Level with topicId [%s] and levelId: [%s] not found", topicId, level.getId());
      log.error(errorMsg);
      throw new ResourceNotFoundException(errorMsg);
    }

    // Only the creator/owner of the level is allowed to modify it
    if (!levelToFind.getCreatedBy().equals(userEmail)) {
      throw new NotAuthorizedException();
    }

    return levelRepository.save(level);
  }

  @Override
  public boolean deleteLevel(Integer topicId, Integer levelId, String userEmail) {
    Level levelToFind = levelRepository.findById(levelId).orElseThrow(() -> {
      String errorMsg = String.format("Level with levelId: [%s] not found", levelId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    if (levelToFind.getTopic().getId() != topicId) {
      String errorMsg = String
          .format("Level with topicId [%s] and levelId: [%s] not found", topicId, levelId);
      log.error(errorMsg);
      throw new ResourceNotFoundException(errorMsg);
    }

    // Only the creator/owner of the level is allowed to modify it
    if (!levelToFind.getCreatedBy().equals(userEmail)) {
      throw new NotAuthorizedException();
    }

    // Delete the level
    levelRepository.delete(levelToFind);

    return true;
  }
}
