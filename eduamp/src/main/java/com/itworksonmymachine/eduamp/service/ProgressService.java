package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Progress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ProgressService {

  Page<Progress> fetchAllProgressByGameMapId(Integer gameMapId, Authentication authentication,
      Pageable pageable);

  Page<Progress> fetchAllProgressByUserEmail(String userEmail, Authentication authentication,
      Pageable pageable);

  Progress fetchProgressByUserEmailAndGameMapId(String userEmail, Integer gameMapId,
      Authentication authentication);

  Progress fetchProgressById(Integer progressId);

  Progress createProgress(String userEmail, Integer gameMapId, Progress progress);

  Progress updateProgress(String userEmail, Integer gameMapId, Progress progress);

  // Progress cannot be deleted
//  boolean deleteProgress(Integer gameMapId, Integer progressId, String userEmail);

}
