package com.itworksonmymachine.eduamp.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itworksonmymachine.eduamp.config.TestConfig;
import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.TopicRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
public class GameMapControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private GameMapRepository gameMapRepository;

  @Autowired
  private TopicRepository topicRepository;

  private GameMap gameMap;

  /**
   * Returns the topic created on the test: should_allowCreateTopic_ifAuthorized.
   *
   * @return Persistent topic.
   */
  private Topic getPersistentTopic() {
    // Test db should only have 1 topic
    Iterable<Topic> allTopics = topicRepository.findAll();
    return allTopics.iterator().next();
  }

  private int getPersistentGameMapId() {
    Iterable<GameMap> allGameMaps = gameMapRepository.findAll();
    return allGameMaps.iterator().next().getId();
  }

  @BeforeEach
  private void setup() {
    this.gameMap = new GameMap();
    this.gameMap.setTitle("This is a title");
    this.gameMap.setDescription("This is a description");
    this.gameMap.setMapDescriptor("This is a map descriptor");
    this.gameMap.setPlayable(false);
  }

  @Test
  @Order(1)
  @WithUserDetails("teacher1@test.com")
  public void createContext() throws Exception {
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
  }

  @Test
  @Order(2)
  @WithUserDetails("teacher1@test.com")
  public void should_allowCreateGameMap_ifAuthorized() throws Exception {

    String gameMapJson = new ObjectMapper().writeValueAsString(this.gameMap);
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/topics/%s/gameMaps/create", getPersistentTopic().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(gameMapJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(3)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectCreateGameMap_ifNotAuthorized() throws Exception {

    String gameMapJson = new ObjectMapper().writeValueAsString(this.gameMap);
    mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/topics/%s/gameMaps/create", getPersistentTopic().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(gameMapJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(4)
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchGameMaps_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .get(String.format("/topics/%s/gameMaps", getPersistentTopic().getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(5)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowFetchGameMaps_ifAuthorized() throws Exception {
    // There will only be 1 topic in the database
    mockMvc.perform(MockMvcRequestBuilders
        .get(String.format("/topics/%s/gameMaps", getPersistentTopic().getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].mapDescriptor", is(this.gameMap.getMapDescriptor())))
        .andExpect(jsonPath("$.content[0].playable", is(this.gameMap.isPlayable())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(6)
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchGameMap_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .get(String
            .format("/topics/%s/gameMaps/%s", getPersistentTopic().getId(),
                getPersistentGameMapId()))
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
  public void should_allowFetchGameMap_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .get(String
            .format("/topics/%s/gameMaps/%s", getPersistentTopic().getId(),
                getPersistentGameMapId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(8)
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectUpdateGameMap_ifNotAuthorized() throws Exception {

    this.gameMap.setMapDescriptor("This is a edited map descriptor");
    String gameMapJson = new ObjectMapper().writeValueAsString(this.gameMap);

    mockMvc.perform(MockMvcRequestBuilders
        .put(String.format("/topics/%s/gameMaps/%s",
            getPersistentTopic().getId(), getPersistentGameMapId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(gameMapJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(9)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowUpdateGameMap_ifAuthorized() throws Exception {

    this.gameMap.setMapDescriptor("This is a edited map descriptor");
    String gameMapJson = new ObjectMapper().writeValueAsString(this.gameMap);

    // There will only be 1 topic in the database
    mockMvc.perform(MockMvcRequestBuilders
        .put(String.format("/topics/%s/gameMaps/%s",
            getPersistentTopic().getId(), getPersistentGameMapId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(gameMapJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mapDescriptor", is(this.gameMap.getMapDescriptor())))
        .andExpect(jsonPath("$.playable", is(this.gameMap.isPlayable())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(10)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectDeleteGameMap_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete(String
        .format("/topics/%s/gameMaps/%s", getPersistentTopic().getId(),
            getPersistentGameMapId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(11)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowDeleteGameMap_ifAuthorized() throws Exception {
    int gameMapId = getPersistentGameMapId();
    mockMvc.perform(MockMvcRequestBuilders.delete(String
        .format("/topics/%s/gameMaps/%s", getPersistentTopic().getId(),
            gameMapId))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    mockMvc.perform(MockMvcRequestBuilders.delete(String
        .format("/topics/%s/gameMaps/%s", getPersistentTopic().getId(),
            gameMapId))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(12)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_rejectDeleteGameMap_ifNotExists() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete(String
        .format("/topics/%s/gameMaps/%s", getPersistentTopic().getId(),
            getPersistentGameMapId()-1))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(9999)
  public void cleanupContext() throws Exception {
    // Delete topic
    // END OF TEST, delete topic
    topicRepository.deleteById(getPersistentTopic().getId());
  }

//  @Test
//  @Order(12)
//  @WithUserDetails("teacher1@test.com")
//  public void should_allowDeleteGameMap_ifAuthorizedAndOwner() throws Exception {
//    // Delete GameMap
//    mockMvc.perform(MockMvcRequestBuilders.delete(String
//        .format("/topics/%s/gameMaps/%s", getPersistentTopic().getId(),
//            getPersistentGameMapId()))
//        .contentType(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk())
//        .andDo(document("{methodName}",
//            preprocessRequest(prettyPrint()),
//            preprocessResponse(prettyPrint())));

//  }

}




