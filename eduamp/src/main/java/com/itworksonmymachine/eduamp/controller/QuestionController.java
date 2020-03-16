package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.Question;
import com.itworksonmymachine.eduamp.service.QuestionService;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/gameMaps"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class QuestionController {

  private final QuestionService questionService;

  public QuestionController(QuestionService questionService) {
    this.questionService = questionService;
  }

  /**
   * Fetch all available Questions belonging to a GameMap.
   *
   * @param pageable  Pagination context
   * @param gameMapId GameMap id that Questions are referenced by
   * @return Questions belonging to a specific gameMapId
   */
  @RequestMapping(method = RequestMethod.GET, path = "/{gameMapId}/questions")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<Question> fetchAllQuestionByGameMapId(
      Pageable pageable,
      @PathVariable(value = "gameMapId") Integer gameMapId
  ) {
    log.info("Fetching all Questions with gameMapId: [{}] and Pageable: [{}]", gameMapId,
        pageable.toString());
    return questionService.fetchAllQuestions(gameMapId, pageable);
  }

  /**
   * Fetch a specific Question by id.
   *
   * @param gameMapId  GameMap id that Question belongs to
   * @param questionId Question id that the Question is referenced by
   * @return question Question with the requested belonging to GameMapId and referenced by the
   * questionId
   */
  @RequestMapping(method = RequestMethod.GET, path = "/gameMap/{gameMapId}/questions/{questionId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Question fetchTopic(
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @PathVariable(value = "questionId") Integer questionId
  ) {
    log.info("Fetching Question with gameMapId: [{}] and questionId: [{}]", gameMapId, questionId);
    return questionService.fetchQuestionById(gameMapId, questionId);
  }

  /**
   * Create a Question.
   *
   * @param gameMapId GameMap id that Question belongs to
   * @param question  Question to be created
   * @return Created Question
   */
  @RequestMapping(method = RequestMethod.POST, path = "/{gameMapId}/questions/create")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Question createQuestion(
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @RequestBody Question question
  ) {
    log.info("Creating question: [{}]", question.toString());
    return questionService.createQuestion(gameMapId, question);
  }

  /**
   * Update a Question.
   * <p>
   *
   * @param gameMapId  GameMap id that Question belongs to
   * @param questionId GameMap id that GameMap is referenced by
   * @param question   Question to be updated
   * @param principal  Principal context containing information of the user submitting the request
   * @return Updated Question
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/{gameMapId}/questions/{questionId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Question updateQuestion(
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @PathVariable(value = "questionId") Integer questionId,
      @RequestBody Question question, Principal principal
  ) {
    question.setId(questionId);
    log.info("Updating question with id: [{}]", questionId);
    return questionService.updateQuestion(gameMapId, question, principal.getName());
  }

  /**
   * Delete a GameMap.
   * <p>
   *
   * @param gameMapId  GameMap id that Question belongs to
   * @param questionId GameMap id that GameMap is referenced by
   * @param principal  Principal context containing information of the user submitting the request
   * @return Flag indicating if request is successful
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/{gameMapId}/questions/{questionId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public boolean deleteGameMap(
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @PathVariable(value = "questionId") Integer questionId,
      Principal principal
  ) {
    log.info("Deleting question with id: [{}]", questionId);
    return questionService.deleteQuestion(gameMapId, questionId, principal.getName());
  }

}