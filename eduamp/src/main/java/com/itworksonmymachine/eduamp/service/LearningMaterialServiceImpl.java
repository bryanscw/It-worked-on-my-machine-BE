package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.exception.ResourceNotFoundException;
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
  public LearningMaterial fetchLearningMaterialById(Integer gameMapId, Integer learningMaterialId) {
    String errorMsg = String
        .format("LearningMaterial with gameMapId [%s] and learningMaterialId [%s] not found",
            gameMapId, learningMaterialId);
    LearningMaterial learningMaterial = learningMaterialRepository
        .findById(learningMaterialId).orElseThrow((() -> new ResourceNotFoundException(errorMsg)));

    // Sanity check: Check if the topicId is the parentId of gameMap
    if (learningMaterial.getGameMap().getId() != gameMapId) {
      throw new ResourceNotFoundException(errorMsg);
    }

    return learningMaterial;
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


  @Override
  public boolean deleteLearningMaterial(Integer gameMapId, Integer learningMaterialId,
      String userEmail) {
    LearningMaterial learningMaterialToFind = learningMaterialRepository
        .findById(learningMaterialId).orElseThrow(() -> {
          String errorMsg = String
              .format("LearningMaterial with learningMaterialId: [%s] not found",
                  learningMaterialId);
          log.error(errorMsg);
          return new ResourceNotFoundException(errorMsg);
        });

    if (learningMaterialToFind.getGameMap().getId() != gameMapId) {
      String errorMsg = String
          .format("LearningMaterial with gameMapId: [%s] and learningMaterialId: [%s] not found",
              gameMapId, learningMaterialId);
      log.error(errorMsg);
      throw new ResourceNotFoundException(errorMsg);
    }

    // Only the creator/owner of the learning material is allowed to modify it
    if (!learningMaterialToFind.getCreatedBy().equals(userEmail)) {
      throw new NotAuthorizedException();
    }

    // Delete the learning Material Id
    learningMaterialRepository.delete(learningMaterialToFind);

    return true;
  }
}
