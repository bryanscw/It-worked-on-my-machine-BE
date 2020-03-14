package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends PagingAndSortingRepository<Question, Integer> {

  Page<Question> findQuestionsByGameMap_Id(Integer gameMapId, Pageable pageable);

}
