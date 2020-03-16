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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/topics"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class TopicController {

  private final TopicService topicService;

  public TopicController(TopicService topicService) {
    this.topicService = topicService;
  }

  /**
   * Fetch all available Topics.
   *
   * @param pageable Pagination context
   * @return All available topics
   */
  @RequestMapping(method = RequestMethod.GET, path = "/")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<Topic> fetchAllTopics(Pageable pageable) {
    log.info("Fetching all topics: [{}]", pageable.toString());
    return topicService.fetchAllTopics(pageable);
  }

  /**
   * Fetch a specific Topic by id.
   *
   * @param topicId Topic id that topic is referenced by
   * @return Topic Topic with the requested topic id.
   */
  @RequestMapping(method = RequestMethod.GET, path = "/{topicId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Topic fetchTopic(@PathVariable(value = "topicId") Integer topicId) {
    log.info("Fetching topic with id: [{}]", topicId);
    return topicService.fetchTopic(topicId);
  }

  /**
   * Create a Topic.
   *
   * @param topic Topic to be created
   * @return Created topic
   */
  @RequestMapping(method = RequestMethod.POST, path = "/create")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Topic createTopic(@RequestBody Topic topic) {
    log.info("Creating topic: [{}]", topic.toString());
    return topicService.createTopic(topic);
  }

  /**
   * Update a Topic.
   * <p>
   *
   * @param topicId   Topic id that topic is referenced by
   * @param topic     Topic to be updated
   * @param principal Principal context containing information of the user submitting the request
   * @return Updated topic
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/{topicId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Topic updateTopic(
      @PathVariable(value = "topicId") Integer topicId,
      @RequestBody Topic topic,
      Principal principal
  ) {
    log.info("Updating topic with id: [{}]", topicId);
    topic.setId(topicId);
    return topicService.updateTopic(topic, principal.getName());
  }

  /**
   * Patch a Topic.
   * <p>
   *
   * @param topicId   Topic id that topic is referenced by
   * @param topic     Topic to be updated
   * @param principal Principal context containing information of the user submitting the request
   * @return Updated topic
   */
  @RequestMapping(method = RequestMethod.PATCH, path = "/{topicId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public Topic patchTopic(
      @PathVariable(value = "topicId") Integer topicId,
      @RequestBody Topic topic, Principal principal
  ) {
    log.info("Patching topic with id: [{}]", topicId);
    topic.setId(topicId);
    return topicService.updateTopic(topic, principal.getName());
  }

  /**
   * Delete a Topic.
   * <p>
   *
   * @param topicId   Topic id that topic is referenced by
   * @param principal Principal context containing information of the user submitting the request
   * @return Flag indicating if request is successful
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/{topicId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public boolean deleteTopic(
      @PathVariable(value = "topicId") Integer topicId,
      Principal principal
  ) {
    log.info("Deleting topic with id: [{}]", topicId);
    return topicService.deleteTopic(topicId, principal.getName());
  }

}