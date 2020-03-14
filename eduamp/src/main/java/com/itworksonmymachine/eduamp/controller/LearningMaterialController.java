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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = {"/gamemaps"},
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class LearningMaterialController {

  private final LearningMaterialService learningMaterialService;

  public LearningMaterialController(LearningMaterialService learningMaterialService) {
    this.learningMaterialService = learningMaterialService;
  }

  /**
   * Fetch all available LearningMaterials.
   *
   * @param pageable  Pagination context
   * @param gameMapId GameMap id that LearningMaterial is referenced by
   * @return LearningMaterials belonging to a specific levelId
   */
  @RequestMapping(method = RequestMethod.GET, path = "/{gameMapId}/learningMaterials/}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
  public Page<LearningMaterial> fetchAllLearningMaterials(Pageable pageable, Integer gameMapId) {
    return learningMaterialService.fetchAllLearningMaterials(gameMapId, pageable);
  }

  /**
   * Fetch a specific LearningMaterial by Id.
   *
   * @param gameMapId          GameMap id that the LearningMaterial belongs to
   * @param learningMaterialId LearningMaterial id that the Learning Material is referenced by
   * @return LeanringMaterial belonging to a specific GameMap id and LearningMaterial id
   */
  @RequestMapping(method = RequestMethod.GET, path = "/{gameMapId}/learningMaterials/{learningMaterialId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public LearningMaterial fetchLearningMaterial(
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @PathVariable(value = "learningMaterialId") Integer learningMaterialId) {
    return learningMaterialService.fetchLearningMaterialById(gameMapId, learningMaterialId);
  }

  /**
   * Create a LearningMaterial.
   *
   * @param gameMapId GameMap id that the question
   * @param learningMaterial LearningMaterial to be created
   * @return Created learningMaterial
   */
  @RequestMapping(method = RequestMethod.POST, path = "/{gameMapId}/learningMaterials/create")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public LearningMaterial createLearningMaterial(@PathVariable(value = "gameMapId") Integer gameMapId, @RequestBody LearningMaterial learningMaterial) {
    return learningMaterialService.createLearningMaterial(gameMapId, learningMaterial);
  }

  /**
   * Update a LearningMaterial.
   *
   * @param learningMaterial LearningMaterial to be updated
   * @param principal        Principal context containing information of the user submitting the
   *                         request
   * @return Updated learningMaterial
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/{gameMapId}/learningMaterials/{learningMaterialId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public LearningMaterial updateLearningMaterial(
      @PathVariable(value = "gameMapId") Integer gameMapId,
      @PathVariable(value = "learningMaterialId") Integer learningMaterialId,
      @RequestBody LearningMaterial learningMaterial, Principal principal) {
    learningMaterial.setId(learningMaterialId);
    return learningMaterialService.updateLearningMaterial(gameMapId, learningMaterial, principal.getName());
  }

  @RequestMapping(method = RequestMethod.DELETE, path = "/gameMapId/learningMaterials/{learningMaterialId}")
  @ResponseStatus(HttpStatus.OK)
  @Secured({"ROLE_TEACHER"})
  public boolean deleteLearningMaterial(@PathVariable(value = "gameMapId") Integer gameMapId,
      @PathVariable(value = "learningMaterialId") Integer learningMaterialId, Principal principal) {
    return learningMaterialService
        .deleteLearningMaterial(gameMapId, learningMaterialId, principal.getName());
  }

}