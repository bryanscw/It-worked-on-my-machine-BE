package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Level;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.repository.LevelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LevelServiceImpl implements LevelService {

  private final LevelRepository levelRepository;

  public LevelServiceImpl(LevelRepository levelRepository) {
    this.levelRepository = levelRepository;
  }

  @Override
  public Page<Level> fetchAllLevels(Pageable pageable, Integer topicId) {
    return levelRepository.findLevelsByTopic_Id(topicId);
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
