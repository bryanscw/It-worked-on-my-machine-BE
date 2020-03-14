package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.GameMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameMapRepository extends PagingAndSortingRepository<GameMap, Integer> {

  Page<GameMap> findGameMapsByTopic_Id(Integer topic_id, Pageable pageable);

}
