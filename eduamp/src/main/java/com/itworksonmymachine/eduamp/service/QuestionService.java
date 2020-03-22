package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface QuestionService {

  Page<Question> fetchAllQuestions(Integer gameMapId, Pageable pageable,
      Authentication authentication);

  Question fetchQuestionById(Integer gameMapId, Integer questionId, Authentication authentication);

  Question createQuestion(Integer gameMapId, Question question);

  Question updateQuestion(Integer gameMapId, Question question, String userEmail);

  boolean deleteQuestion(Integer gameMapId, Integer questionId, String userEmail);

}
