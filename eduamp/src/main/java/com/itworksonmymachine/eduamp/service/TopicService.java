package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopicService {

  Page<Topic> fetchAllTopics(Pageable pageable);

  Topic createTopic(Topic topic);

  Topic updateTopic(Topic topic, String userEmail);

}
