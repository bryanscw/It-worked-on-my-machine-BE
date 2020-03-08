package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.Topic;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends PagingAndSortingRepository<Topic, Integer> {

}
