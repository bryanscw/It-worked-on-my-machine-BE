package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.entity.Question;
import com.itworksonmymachine.eduamp.exception.ResourceNotFoundException;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.QuestionRepository;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

  private final GameMapRepository gameMapRepository;

  private final QuestionRepository questionRepository;

  public QuestionServiceImpl(GameMapRepository gameMapRepository,
      QuestionRepository questionRepository) {
    this.gameMapRepository = gameMapRepository;
    this.questionRepository = questionRepository;
  }

  @Override
  public Page<Question> fetchAllQuestions(Integer gameMapId, Pageable pageable,
      Authentication authentication) {
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Page<Question> questions = questionRepository.findQuestionsByGameMap_Id(gameMapId, pageable);

    // Remove answer if request is from a user
    if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
      questions.stream().forEach(question -> {
        question.setAnswer(-1);
      });
    }

    return questions;
  }

  @Override
  public Question fetchQuestionById(Integer gameMapId, Integer questionId,
      Authentication authentication) {
    Question questionToFind = questionRepository.findById(questionId).orElseThrow(() -> {
      String errorMsg = String.format("Question with questionId: [%s] not found", questionId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    // Sanity check: Check if the gameMapId is the parentId of Question
    if (questionToFind.getGameMap().getId() != gameMapId) {
      String errorMsg = String
          .format("Question with gameMapId: [%s] and questionId: [%s] not found", gameMapId,
              questionId);
      log.error(errorMsg);
      throw new ResourceNotFoundException(errorMsg);
    }

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    // Remove answer if request is from a user
    if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
      questionToFind.setAnswer(-1);
    }
    return questionToFind;
  }

  @Override
  public Question createQuestion(Integer gameMapId, Question question) {
    GameMap gameMapToFind = gameMapRepository.findById(gameMapId).orElseThrow(() -> {
      String errorMsg = String.format("GameMap with gameMapId: [%s] not found", gameMapId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    question.setGameMap(gameMapToFind);
    return questionRepository.save(question);
  }

  @Override
  public Question updateQuestion(Integer gameMapId, Question question, String userEmail) {
    Question questionToFind = questionRepository.findById(question.getId()).orElseThrow(() -> {
      String errorMsg = String.format("Question with questionId: [%s] not found", question.getId());
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    if (questionToFind.getGameMap().getId() != gameMapId) {
      String errorMsg = String
          .format("Question with gameMapId: [%s] and questionId: [%s] not found", gameMapId,
              question.getId());
      log.error(errorMsg);
      throw new ResourceNotFoundException(errorMsg);
    }

//    // Only the creator/owner of the level is allowed to modify it
//    if (!gameMapToFind.getCreatedBy().equals(userEmail)) {
//      throw new NotAuthorizedException();
//    }

    if (question.getAnswer() != -1) {
      questionToFind.setAnswer(question.getAnswer());
    }

    if (question.getCoordinates() != null) {
      questionToFind.setCoordinates(question.getCoordinates());
    }

    if (question.getOptions() != null) {
      questionToFind.setOptions(question.getOptions());
    }

    if (question.getQuestionText() != null) {
      questionToFind.setQuestionText(question.getQuestionText());
    }

    return questionRepository.save(questionToFind);
  }

  @Override
  public boolean deleteQuestion(Integer gameMapId, Integer questionId, String userEmail) {
    Question questionToFind = questionRepository.findById(questionId).orElseThrow(() -> {
      String errorMsg = String.format("Question with questionId: [%s] not found", questionId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    if (questionToFind.getGameMap().getId() != gameMapId) {
      String errorMsg = String
          .format("Question with gameMapId: [%s] and questionId: [%s] not found", gameMapId,
              questionId);
      log.error(errorMsg);
      throw new ResourceNotFoundException(errorMsg);
    }

//    // Only the creator/owner of the level is allowed to modify it
//    if (!gameMapToFind.getCreatedBy().equals(userEmail)) {
//      throw new NotAuthorizedException();
//    }

    // Delete the GameMap
    questionRepository.delete(questionToFind);

    return true;
  }
}
