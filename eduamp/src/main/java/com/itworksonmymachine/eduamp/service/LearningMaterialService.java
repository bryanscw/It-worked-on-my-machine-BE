package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LearningMaterialService {

  Page<LearningMaterial> fetchAllLearningMaterials(Integer gameMapId, Pageable pageable);

  LearningMaterial createLearningMaterial(LearningMaterial learningMaterial);

  LearningMaterial updateLearningMaterial(LearningMaterial learningMaterial, String userEmail);

}
