package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import com.itworksonmymachine.eduamp.service.LearningMaterialService;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/learningMaterial"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class LearningMaterialController {

  private final LearningMaterialService learningMaterialService;

  public LearningMaterialController(LearningMaterialService learningMaterialService) {
    this.learningMaterialService = learningMaterialService;
  }

  /**
   * Fetch all available levels.
   *
   * @param pageable Pagination context
   * @param levelId  Level id that LearningMaterial is referenced by
   * @return LearningMaterials belonging to a specific levelId
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<LearningMaterial> fetchAllLearningMaterials(Pageable pageable, Integer levelId) {
    return learningMaterialService.fetchAllLearningMaterials(pageable, levelId);
  }

  /**
   * Create a LearningMaterial.
   *
   * @param learningMaterial LearningMaterial to be created
   * @return Created learningMaterial
   */
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public LearningMaterial createLearningMaterial(@RequestBody LearningMaterial learningMaterial) {
    return learningMaterialService.createLearningMaterial(learningMaterial);
  }

  /**
   * Update a topic. Only the creator of the topic is allowed to modify it.
   *
   * @param learningMaterial LearningMaterial to be updated
   * @param principal        Principal context containing information of the user submitting the
   *                         request
   * @return Updated learningMaterial
   */
  @RequestMapping(method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public LearningMaterial updateLearningMaterial(@RequestBody LearningMaterial learningMaterial,
      Principal principal) {
    return learningMaterialService.updateLearningMaterial(learningMaterial, principal.getName());
  }

}