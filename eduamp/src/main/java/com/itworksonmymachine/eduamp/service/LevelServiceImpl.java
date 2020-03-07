package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Level;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.exception.ResourceNotFoundException;
import com.itworksonmymachine.eduamp.repository.LevelRepository;
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

  public LevelServiceImpl(LevelRepository levelRepository) {
    this.levelRepository = levelRepository;
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
  public Level createLevel(Level level) {
    return levelRepository.save(level);
  }

  @Override
  public Level updateLevel(Level level, String userEmail) {
    // Only the creator/owner of the level is allowed to modify it
    if (!level.getCreatedBy().equals(userEmail)) {
      throw new NotAuthorizedException();
    }
    return levelRepository.save(level);
  }

}
