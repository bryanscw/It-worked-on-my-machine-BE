package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends PagingAndSortingRepository<Level, Integer> {

  Page<Level> findLevelsByTopic_Id(Pageable pageable, Integer id);

}
