package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.exception.ResourceNotFoundException;
import com.itworksonmymachine.eduamp.repository.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TopicServiceImpl implements TopicService {

  private final TopicRepository topicRepository;

  public TopicServiceImpl(TopicRepository topicRepository) {
    this.topicRepository = topicRepository;
  }

  @Override
  public Page<Topic> fetchAllTopics(Pageable pageable) {
    return topicRepository.findAll(pageable);
  }

  @Override
  public Topic fetchTopic(Integer topicId) {
    log.info("Fetching topic with topicId: [{}]", topicId);
    return topicRepository.findById(topicId).orElseThrow(() -> {
      String errorMsg = String.format("Topic with topicId: [%s] not found", topicId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });
  }

  @Override
  public Topic createTopic(Topic topic) {
    return topicRepository.save(topic);
  }

  @Override
  public Topic updateTopic(Topic topic, String userEmail) {
    Topic topicToFind = topicRepository.findById(topic.getId()).orElseThrow(() -> {
      String errorMsg = String.format("Topic with topicId: [%s] not found", topic.getId());
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

//    // Only the creator/owner of the topic is allowed to modify it
//    if (!topicToFind.getCreatedBy().equals(userEmail)) {
//      throw new NotAuthorizedException();
//    }

    // Facilitate partial updating (PATCH)
    if (topic.getDescription() != null) {
      topicToFind.setDescription(topic.getDescription());
    }

    if (topic.getTitle() != null) {
      topicToFind.setTitle(topic.getTitle());
    }

    return topicRepository.save(topicToFind);
  }

  @Override
  public boolean deleteTopic(Integer topicId, String userEmail) {
    Topic topicToFind = topicRepository.findById(topicId).orElseThrow(() -> {
      String errorMsg = String.format("Topic with topicId: [%s] not found", topicId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

//    // Only the creator/owner of the topic is allowed to modify it
//    if (!topicToFind.getCreatedBy().equals(userEmail)) {
//      throw new NotAuthorizedException();
//    }

    // Delete the topic
    topicRepository.delete(topicToFind);

    return true;
  }
}
