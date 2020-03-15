package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.Progress;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRepository extends PagingAndSortingRepository<Progress, Integer> {

  Optional<Progress> findProgressByUser_EmailAndMap_Id(String userEmail, Integer gameMapId);

  Page<Progress> findProgressByUser_EmailAndMap_Id(String userEmail, Integer gameMapId,
      Pageable pageable);

  Page<Progress> findProgressByMap_Id(Integer gameMapId, Pageable pageable);

  Page<Progress> findProgressByUser_Email(String userEmail, Pageable pageable);

}
