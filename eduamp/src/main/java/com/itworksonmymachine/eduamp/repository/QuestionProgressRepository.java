package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.QuestionProgress;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QuestionProgressRepository extends
    PagingAndSortingRepository<QuestionProgress, Integer> {

}
