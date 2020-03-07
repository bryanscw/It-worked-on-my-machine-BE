package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LearningMaterialService {

  Page<LearningMaterial> fetchAllLearningMaterials(Pageable pageable, Integer levelId);

  LearningMaterial createLearningMaterial(LearningMaterial learningMaterial);

  LearningMaterial updateLearningMaterial(LearningMaterial learningMaterial, String userEmail);

}
