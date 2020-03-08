package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LevelService {

  Page<Level> fetchAllLevels(Pageable pageable, Integer topicId);

  Level fetchLevelById(Integer topicId, Integer levelId);

  Level createLevel(Level level);

  Level updateLevel(Level level, String userEmail);

}
