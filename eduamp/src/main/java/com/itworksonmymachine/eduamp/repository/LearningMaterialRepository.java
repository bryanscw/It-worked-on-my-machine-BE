package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningMaterialRepository extends
    PagingAndSortingRepository<LearningMaterial, Integer> {

  Page<LearningMaterial> findLearningMaterialsByLevel_Id(Pageable pageable, Integer id);

}
