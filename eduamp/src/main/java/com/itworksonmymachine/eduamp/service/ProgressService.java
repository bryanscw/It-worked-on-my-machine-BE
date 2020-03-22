package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Progress;
import com.itworksonmymachine.eduamp.model.dto.LeaderboardResultDTO;
import com.itworksonmymachine.eduamp.model.dto.QuestionAttemptDTO;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ProgressService {

  Page<Progress> fetchAllProgressByGameMapId(Integer gameMapId, Authentication authentication,
      Pageable pageable);

  ArrayList<LeaderboardResultDTO> fetchLeaderboardByGameMapId(Integer gameMapId);

  ArrayList<QuestionAttemptDTO> fetchAttemptCountByGameMapId(Integer gameMapId);

  Page<Progress> fetchAllProgressByUserEmail(String userEmail, Authentication authentication,
      Pageable pageable);

  Progress fetchProgressByUserEmailAndGameMapId(String userEmail, Integer gameMapId,
      Authentication authentication);

  Progress createProgress(String userEmail, Integer gameMapId, Progress progress,
      Authentication authentication);

  Progress updateProgress(String userEmail, Integer gameMapId, Progress progress,
      Authentication authentication);

  boolean checkAnswer(String userEmail, Integer gameMapId, Integer questionId, Integer answer,
      Authentication authentication);

  // Progress cannot be deleted
//  boolean deleteProgress(Integer gameMapId, Integer progressId, String userEmail);

}
