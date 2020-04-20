package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.QuestionProgress;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionProgressRepository extends
    PagingAndSortingRepository<QuestionProgress, Integer> {

  Optional<QuestionProgress> findQuestionProgressByQuestion_IdAndProgress_Id(int questionId,
      int progressId);

}
