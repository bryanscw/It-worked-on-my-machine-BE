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
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestConfig.class
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class LearningMaterialControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private GameMapRepository gameMapRepository;

  @Autowired
  private TopicRepository topicRepository;

  private LearningMaterial learningMaterial;

  private GameMap gameMap;

  private GameMap getPersistentGameMap() {
    Iterable<GameMap> allGameMaps = gameMapRepository.findAll();
    return allGameMaps.iterator().next();
  }

  private Topic getPersistentTopic(){
    Iterable<Topic> allTopics = topicRepository.findAll();
    return allTopics.iterator().next();
  }

  @BeforeEach
  private void setUp(){
    this.gameMap = new GameMap();
    this.gameMap.setMapDescriptor("This is a map descriptor");
    this.gameMap.setPlayable(false);

    this.learningMaterial = new LearningMaterial();
    this.learningMaterial.setGameMap(this.gameMap);
    this.learningMaterial.setTitle("[Topic Title]: Multiplication");
    this.learningMaterial.setDescription("[Topic Description]: This topic is about simple multiplication");
    this.learningMaterial.setLink("http://test.com");
  }

  @Test
  @Order(1)
  @WithUserDetails("teacher1@test.com")
  public void should_allowCreateLearningMaterial_ifAuthorized() throws Exception {
    Topic topic = new Topic();
    topic.setTitle("[Topic Title]: Multiplication");
    topic.setDescription("[Topic Description]: This topic is about simple multiplication");

    // We want principal information to be saved, do not use TopicRepository directly
    String topicJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(topic);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/topics/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(topicJson))
        .andExpect(status().isOk()).andReturn();

    String gameMapJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(this.learningMaterial.getGameMap());
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/topics/%s/gamemaps/create", getPersistentTopic().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(gameMapJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/%s/learningmaterials/create", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(learningMaterialJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(2)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectCreateLearningMaterial_ifNotAuthorized() throws Exception {

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/%s/learningmaterials/create", getPersistentGameMap().getId()))
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
  public void should_rejectCreateLearningMaterial_ifAlreadyExist() throws Exception {
    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/%s/learningmaterials/create", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(learningMaterialJson))
        .andExpect(status().isBadRequest())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(4)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowFetchLearningMaterial_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/%s/learningmaterials/%s",
                    getPersistentGameMap().getId(), this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(this.learningMaterial.getTitle())))
        .andExpect(jsonPath("$.description", is(this.learningMaterial.getDescription())))
        .andExpect(jsonPath("$.link", is(this.learningMaterial.getLink())))
        .andExpect(jsonPath("$.gameMapId", is(this.learningMaterial.getGameMap().getId())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(5)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectFetchLearningMaterial_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/%s/learningmaterials/%s",
                    getPersistentGameMap().getId(), this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(6)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_allowFetchLearningMaterials_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/%s/learningmaterials/%s",
        getPersistentGameMap().getId(), this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(this.learningMaterial.getTitle())))
        .andExpect(jsonPath("$.description", is(this.learningMaterial.getDescription())))
        .andExpect(jsonPath("$.link", is(this.learningMaterial.getLink())))
        .andExpect(jsonPath("$.gameMapId", is(this.learningMaterial.getGameMap().getId())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(7)
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchLearningMaterials_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/%s/learningmaterials/%s",
        getPersistentGameMap().getId(), this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(8)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectUpdateLearningMaterial_ifNotAuthorized() throws Exception {
    this.learningMaterial.setTitle("New title");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(MockMvcRequestBuilders.put(String.format("/%s/learningmaterials/%s",
                    getPersistentGameMap().getId(),
                    this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(learningMaterialJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(9)
  @WithUserDetails("teacher2@test.com")
  @Transactional
  public void should_rejectUpdateLearningMaterial_ifNotOwner() throws Exception {
    this.learningMaterial.setTitle("New title");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(MockMvcRequestBuilders.put(String.format("/%s/learningmaterials/%s",
                    getPersistentGameMap().getId(),
                    this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(learningMaterialJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(10)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowUpdateLearningMaterial_ifAuthorized() throws Exception {
    this.learningMaterial.setTitle("New title");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(MockMvcRequestBuilders.put(String.format("/%s/learningmaterials/%s",
                    getPersistentGameMap().getId(),
                    this.learningMaterial.getId()))
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
  @Order(11)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectDeleteLearningMaterial_ifNotAuthorized() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/%s/learningmaterials/%s",
                    getPersistentGameMap().getId(),
                    this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    // Delete topic
    topicRepository.deleteById(getPersistentTopic().getId());
  }

  @Test
  @Order(12)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowDeleteLearningMaterial_ifNotAuthorized() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/%s/learningmaterials/%s",
        getPersistentGameMap().getId(),
        this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    // Delete topic
    topicRepository.deleteById(getPersistentTopic().getId());
  }

  @Test
  @Order(13)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_rejectDeleteLearningMaterial_ifNotExist() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/%s/learningmaterials/%s",
        getPersistentGameMap().getId(),
        this.learningMaterial.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }
}


