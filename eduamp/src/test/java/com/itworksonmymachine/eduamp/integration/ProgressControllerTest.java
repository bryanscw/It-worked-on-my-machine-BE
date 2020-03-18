package com.itworksonmymachine.eduamp.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itworksonmymachine.eduamp.config.MockUserClass;
import com.itworksonmymachine.eduamp.config.TestConfig;
import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.entity.Progress;
import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.model.Coordinates;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.ProgressRepository;
import com.itworksonmymachine.eduamp.repository.TopicRepository;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import javax.swing.text.StyledEditorKit.ItalicAction;
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
public class ProgressControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private GameMapRepository gameMapRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProgressRepository progressRepository;

  private MockUserClass user;

  private Progress progress;

  public Topic getPersistentTopic(){
    Iterable<Topic> allTopics = topicRepository.findAll();
    return allTopics.iterator().next();
  }

  public GameMap getPersistentGameMap(){
    Iterable<GameMap> allGameMaps = gameMapRepository.findAll();
    return allGameMaps.iterator().next();
  }

  public User getPersistentUser(){
    Iterable<User> allUsers = userRepository.findAll();
    return allUsers.iterator().next();
  }

  public Progress getPersistentProgress(){
    Iterable<Progress> allProgress = progressRepository.findAll();
    return allProgress.iterator().next();
  }

  @BeforeEach
  public void setUp(){
    Coordinates coordinates = new Coordinates();
    coordinates.setX(1);
    coordinates.setY(1);

    this.progress = new Progress();
    this.progress.setPosition(coordinates);
    this.progress.setTimeTaken(0);
  }

  @Test
  @Order(1)
  @WithUserDetails("teacher1@test.com")
  public void createGameMap() throws Exception {
    Topic topic = new Topic();
    topic.setTitle("[Topic Title]: Multiplication");
    topic.setDescription("[Topic Description]: This topic is about simple multiplication");

    // We want principal information to be saved, do not use TopicRepository directly
    String topicJson = new ObjectMapper().writeValueAsString(topic);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/topics/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(topicJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint())));

    GameMap gameMap = new GameMap();
    gameMap.setTitle("Game map title");
    gameMap.setDescription("Test description");
    gameMap.setMapDescriptor("Test map description");
    gameMap.setPlayable(true);

    String gameMapJson = new ObjectMapper().writeValueAsString(gameMap);
    mockMvc.perform(
          MockMvcRequestBuilders
            .post(String.format("/topics/%s/gameMaps/create", getPersistentTopic().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(gameMapJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    User user = new User();
    user.setEmail("admin1@test.com");
    user.setName("admin");
    user.setRole("ROLE_ADMIN");
    user.setPass("pass");
    userRepository.save(user);
  }

  @Order(2)
  @Test
  @WithUserDetails("admin1@test.com")
  public void should_allowCreateProgress_ifAuthorized() throws Exception {

    String progressJson = new ObjectMapper().writeValueAsString(this.progress);
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/progress/users/%s/gameMaps/%s",
                "admin1@test.com", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(progressJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(3)
  @Test
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectCreateProgress_ifNotAuthorized() throws Exception {

    String progressJson = new ObjectMapper().writeValueAsString(this.progress);
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/progress/users/%s/gameMaps/%s",
                "user1@test.com", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(progressJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(4)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_allowFetchProgressByUserEmail_ifAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s", "admin1@test.com"))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].position[\"x\"]", is(this.progress.getPosition().getX())))
        .andExpect(jsonPath("$.content[0].position[\"y\"]", is(this.progress.getPosition().getY())))
        .andExpect(jsonPath("$.content[0].user.email", is(getPersistentProgress().getUser().getEmail())))
        .andExpect(jsonPath("$.content[0].user.role", is(getPersistentProgress().getUser().getRole())))
        .andExpect(jsonPath("$.content[0].user.name", is(getPersistentProgress().getUser().getName())))
        .andExpect(jsonPath("$.content[0].map.title", is(getPersistentProgress().getMap().getTitle())))
        .andExpect(jsonPath("$.content[0].map.description", is(getPersistentProgress().getMap().getDescription())))
        .andExpect(jsonPath("$.content[0].map.mapDescriptor", is(getPersistentProgress().getMap().getMapDescriptor())))
        .andExpect(jsonPath("$.content[0].timeTaken", is(getPersistentProgress().getTimeTaken())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(5)
  @Test
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchProgressByUserEmail_ifNotAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s", "user1@test.com"))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(6)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_allowFetchProgressByGameMapId_ifAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/gameMaps/%s", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].position[\"x\"]", is(this.progress.getPosition().getX())))
        .andExpect(jsonPath("$.content[0].position[\"y\"]", is(this.progress.getPosition().getY())))
        .andExpect(jsonPath("$.content[0].user.email", is(getPersistentProgress().getUser().getEmail())))
        .andExpect(jsonPath("$.content[0].user.role", is(getPersistentProgress().getUser().getRole())))
        .andExpect(jsonPath("$.content[0].user.name", is(getPersistentProgress().getUser().getName())))
        .andExpect(jsonPath("$.content[0].map.title", is(getPersistentProgress().getMap().getTitle())))
        .andExpect(jsonPath("$.content[0].map.description", is(getPersistentProgress().getMap().getDescription())))
        .andExpect(jsonPath("$.content[0].map.mapDescriptor", is(getPersistentProgress().getMap().getMapDescriptor())))
        .andExpect(jsonPath("$.content[0].timeTaken", is(getPersistentProgress().getTimeTaken())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(7)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_rejectFetchProgressByGameMapId_ifNotExists() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/gameMaps/%s", getPersistentGameMap().getId()-1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(8)
  @Test
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchProgressByGameMapId_ifNotAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/gameMaps/%s", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(9)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_allowFetchProgressByUserEmailAndGameMapId_ifAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s/gameMaps/%s",
                "admin1@test.com", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.position[\"x\"]", is(this.progress.getPosition().getX())))
        .andExpect(jsonPath("$.position[\"y\"]", is(this.progress.getPosition().getY())))
        .andExpect(jsonPath("$.user.email", is(getPersistentProgress().getUser().getEmail())))
        .andExpect(jsonPath("$.user.role", is(getPersistentProgress().getUser().getRole())))
        .andExpect(jsonPath("$.user.name", is(getPersistentProgress().getUser().getName())))
        .andExpect(jsonPath("$.map.title", is(getPersistentProgress().getMap().getTitle())))
        .andExpect(jsonPath("$.map.description", is(getPersistentProgress().getMap().getDescription())))
        .andExpect(jsonPath("$.map.mapDescriptor", is(getPersistentProgress().getMap().getMapDescriptor())))
        .andExpect(jsonPath("$.timeTaken", is(getPersistentProgress().getTimeTaken())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(10)
  @Test
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchProgressByUserEmailAndGameMapId_ifNotAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s/gameMaps/%s",
                "user1@test.com", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(11)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_rejectFetchProgressByUserEmailAndGameMapId_ifGameMapNotExists() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s/gameMaps/%s",
                "admin1@test.com", getPersistentGameMap().getId()-1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(12)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_rejectFetchProgressByUserEmailAndGameMapId_ifUserEmailNotExists() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s/gameMaps/%s",
                "admin0@test.com", getPersistentGameMap().getId()-1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(13)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_allowUpdateProgress_ifAuthorized() throws Exception {

    this.progress.setTimeTaken(10.0);

    String progressJson = new ObjectMapper().writeValueAsString(this.progress);
    mockMvc.perform(
        MockMvcRequestBuilders
            .put(String.format("/progress/users/%s/gameMaps/%s",
                "admin1@test.com", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(progressJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(14)
  @Test
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectUpdateProgress_ifNotAuthorized() throws Exception {

    this.progress.setTimeTaken(10.0);

    String progressJson = new ObjectMapper().writeValueAsString(this.progress);
    mockMvc.perform(
        MockMvcRequestBuilders
            .put(String.format("/progress/users/%s/gameMaps/%s",
                "user1@test.com", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(progressJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    progressRepository.deleteById(getPersistentProgress().getId());
    userRepository.deleteUserByEmail(this.user.getEmail());
    gameMapRepository.deleteById(getPersistentGameMap().getId());
    topicRepository.deleteById(getPersistentTopic().getId());
  }
}

