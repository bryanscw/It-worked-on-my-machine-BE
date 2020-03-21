package com.itworksonmymachine.eduamp.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.itworksonmymachine.eduamp.config.TestConfig;
import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.LearningMaterialRepository;
import com.itworksonmymachine.eduamp.repository.TopicRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestConfig.class
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(OrderAnnotation.class)
public class LearningMaterialControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private GameMapRepository gameMapRepository;

  @Autowired
  private LearningMaterialRepository learningMaterialRepository;

  @Autowired
  private TopicRepository topicRepository;

  private LearningMaterial learningMaterial;

  private Topic getPersistentTopic() {
    Iterable<Topic> allTopics = topicRepository.findAll();
    return allTopics.iterator().next();
  }

  private GameMap getPersistentGameMap() {
    Iterable<GameMap> allGameMaps = gameMapRepository.findAll();
    return allGameMaps.iterator().next();
  }

  private int getPersistentLearningMaterialId() {
    Iterable<LearningMaterial> allLearningMaterials = learningMaterialRepository.findAll();
    return allLearningMaterials.iterator().next().getId();
  }

  @BeforeEach
  private void setUp() {
    this.learningMaterial = new LearningMaterial();
    this.learningMaterial.setTitle("[Topic Title]: Multiplication");
    this.learningMaterial
        .setDescription("[Topic Description]: This topic is about simple multiplication");
    this.learningMaterial.setLink("http://test.com");
  }

  @Test
  @Order(-1)
  @WithUserDetails("teacher1@test.com")
  public void setupContext() throws Exception {
    Topic topic = new Topic();
    topic.setTitle("[Topic Title]: Multiplication");
    topic.setDescription("[Topic Description]: This topic is about simple multiplication");

    // We want principal information to be saved, do not use TopicRepository directly
    String topicJson = new ObjectMapper().writeValueAsString(topic);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/topics/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(topicJson))
        .andExpect(status().isOk()).andReturn();

    GameMap gameMap = new GameMap();
    gameMap.setTitle("This is a title");
    gameMap.setDescription("This is a description");
    gameMap.setMapDescriptor("This is a map descriptor");
    gameMap.setPlayable(false);

    // Required during test as ObjectMapper cannot have a non-null Topic.
    // In actual production, Topic can be null
    topic = getPersistentTopic();
    gameMap.setTopic(topic);

    // Create Game Map
    String gameMapJson = new ObjectMapper().writeValueAsString(gameMap);
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/topics/%s/gameMaps/create", getPersistentTopic().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(gameMapJson))
        .andExpect(status().isOk());
  }

  @Test
  @Order(1)
  @WithUserDetails("teacher1@test.com")
  public void should_allowCreateLearningMaterial_ifAuthorized() throws Exception {
    // Required during test as ObjectMapper cannot have a non-null GameMap.
    // In actual production, GameMap can be null
    GameMap gameMap = getPersistentGameMap();
    this.learningMaterial.setGameMap(gameMap);

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(
        MockMvcRequestBuilders.post(
            String.format("/gameMaps/%s/learningMaterials/create", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(learningMaterialJson))
        .andExpect(status().isOk());
  }

  @Test
  @Order(2)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectCreateLearningMaterial_ifNotAuthorized() throws Exception {
    // Required during test as ObjectMapper cannot have a non-null GameMap.
    // In actual production, GameMap can be null
    GameMap gameMap = getPersistentGameMap();
    this.learningMaterial.setGameMap(gameMap);

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(
        MockMvcRequestBuilders.post(
            String.format("/gameMaps/%s/learningMaterials/create", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(learningMaterialJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(3)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowFetchLearningMaterial_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/gameMaps/%s/learningMaterials/%s",
        getPersistentGameMap().getId(), getPersistentLearningMaterialId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(this.learningMaterial.getTitle())))
        .andExpect(jsonPath("$.description", is(this.learningMaterial.getDescription())))
        .andExpect(jsonPath("$.link", is(this.learningMaterial.getLink())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(4)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectFetchLearningMaterial_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/gameMaps/%s/learningMaterials/%s",
        getPersistentGameMap().getId(), getPersistentLearningMaterialId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(5)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_allowFetchLearningMaterials_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/gameMaps/%s/learningMaterials/",
        getPersistentGameMap().getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].title", is(this.learningMaterial.getTitle())))
        .andExpect(jsonPath("$.content[0].link", is(this.learningMaterial.getLink())))
        .andExpect(jsonPath("$.content[0].description", is(this.learningMaterial.getDescription())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(6)
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchLearningMaterials_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/gameMaps/%s/learningMaterials/",
        getPersistentGameMap().getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(7)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectUpdateLearningMaterial_ifNotAuthorized() throws Exception {
    // Required during test as ObjectMapper cannot have a non-null GameMap.
    // In actual production, GameMap can be null
    GameMap gameMap = getPersistentGameMap();
    this.learningMaterial.setGameMap(gameMap);

    this.learningMaterial.setTitle("New title");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(MockMvcRequestBuilders.put(String.format("/gameMaps/%s/learningMaterials/%s",
        getPersistentGameMap().getId(),
        getPersistentLearningMaterialId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(learningMaterialJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(8)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowUpdateLearningMaterial_ifAuthorized() throws Exception {
    // Required during test as ObjectMapper cannot have a non-null GameMap.
    // In actual production, GameMap can be null
    GameMap gameMap = getPersistentGameMap();
    this.learningMaterial.setGameMap(gameMap);

    this.learningMaterial.setTitle("New title");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(MockMvcRequestBuilders.put(String.format("/gameMaps/%s/learningMaterials/%s",
        getPersistentGameMap().getId(),
        getPersistentLearningMaterialId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(learningMaterialJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(this.learningMaterial.getTitle())))
        .andExpect(jsonPath("$.description", is(this.learningMaterial.getDescription())))
        .andExpect(jsonPath("$.link", is(this.learningMaterial.getLink())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(9)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectDeleteLearningMaterial_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/gameMaps/%s/learningMaterials/%s",
        getPersistentGameMap().getId(),
        getPersistentLearningMaterialId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(10)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowDeleteLearningMaterial_ifAuthorized() throws Exception {
    int persistentLearningMaterialId = getPersistentLearningMaterialId();
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/gameMaps/%s/learningMaterials/%s",
        getPersistentGameMap().getId(),
        persistentLearningMaterialId))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/gameMaps/%s/learningMaterials/%s",
        getPersistentGameMap().getId(),
        persistentLearningMaterialId))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(11)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_rejectDeleteLearningMaterial_ifNotExists() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/gameMaps/%s/learningMaterials/%s",
        getPersistentGameMap().getId(),
        getPersistentLearningMaterialId() - 1))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(9999)
  public void cleanupContext() {
    // Delete topic
    gameMapRepository.deleteById(getPersistentGameMap().getId());
    topicRepository.deleteById(getPersistentTopic().getId());
  }

}




