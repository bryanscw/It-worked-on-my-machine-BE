package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.repository.LearningMaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LearningMaterialServiceImpl implements LearningMaterialService {

  private final LearningMaterialRepository learningMaterialRepository;

  public LearningMaterialServiceImpl(LearningMaterialRepository learningMaterialRepository) {
    this.learningMaterialRepository = learningMaterialRepository;
  }

  @Override
  public Page<LearningMaterial> fetchAllLearningMaterials(Integer gameMapId, Pageable pageable) {
    return learningMaterialRepository.findLearningMaterialsByGameMap_Id(gameMapId, pageable);
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
