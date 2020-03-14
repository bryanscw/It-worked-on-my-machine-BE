package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

  Page<Question> fetchAllQuestions(Integer gameMapId, Pageable pageable);

  Question fetchQuestionById(Integer gameMapId, Integer questionId);

  Question createQuestion(Integer gameMapId, Question question);

  Question updateQuestion(Integer gameMapId, Question question, String userEmail);

  boolean deleteQuestion(Integer gameMapId, Integer questionid, String userEmail);

}
