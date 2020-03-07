package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.service.TopicService;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/topic"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class TopicController {

  private final TopicService topicService;

  public TopicController(TopicService topicService) {
    this.topicService = topicService;
  }

  /**
   * Fetch all available topics.
   *
   * @param pageable
   * @return
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<Topic> fetchAllTopics(Pageable pageable) {
    return topicService.fetchAllTopics(pageable);
  }

  /**
   * Create a topic.
   *
   * @param topic Topic to be created
   * @return Created topic
   */
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Topic createTopic(Topic topic) {
    return topicService.createTopic(topic);
  }

  /**
   * Update a topic.
   * Only the creator of the topic is allowed to modify it.
   *
   * @param topic Topic to be updated
   * @return Updated topic
   */
  @RequestMapping(method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Topic updateTopic(Topic topic, Principal principal) {
    return topicService.updateTopic(topic, principal.getName());
  }

}