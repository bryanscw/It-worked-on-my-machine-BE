package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.repository.LearningMaterialRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class LearningMaterialServiceImpl implements LearningMaterialService {

  private final LearningMaterialRepository learningMaterialRepository;

  public LearningMaterialServiceImpl(LearningMaterialRepository learningMaterialRepository) {
    this.learningMaterialRepository = learningMaterialRepository;
  }

  @Override
  public Page<LearningMaterial> fetchAllLearningMaterials(Pageable pageable, Integer levelId) {
    return learningMaterialRepository.findLearningMaterialsByLevel_Id(pageable, levelId);
  }

  @Override
  public LearningMaterial createLearningMaterial(LearningMaterial learningMaterial) {
    return learningMaterialRepository.save(learningMaterial);
  }

  @Override
  public LearningMaterial updateLearningMaterial(LearningMaterial learningMaterial,
      String userEmail) {
    // Only the creator/owner of the learningMaterial is allowed to modify it
    if (!learningMaterial.getCreatedBy().equals(userEmail)) {
      throw new NotAuthorizedException();
    }
    return learningMaterialRepository.save(learningMaterial);
  }

}
