package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
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
  public Topic createTopic(Topic topic) {
    return topicRepository.save(topic);
  }

  @Override
  public Topic updateTopic(Topic topic, String userEmail) {
    // Only the creator/owner of the topic is allowed to modify it
    if (!topic.getCreatedBy().equals(userEmail)) {
      throw new NotAuthorizedException();
    }
    return topicRepository.save(topic);
  }
  
}
