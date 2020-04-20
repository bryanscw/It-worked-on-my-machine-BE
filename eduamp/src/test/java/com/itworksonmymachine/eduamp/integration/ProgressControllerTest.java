package com.itworksonmymachine.eduamp.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import com.itworksonmymachine.eduamp.entity.Question;
import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.model.Coordinates;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.ProgressRepository;
import com.itworksonmymachine.eduamp.repository.QuestionRepository;
import com.itworksonmymachine.eduamp.repository.TopicRepository;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Base64Utils;

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

  @Autowired
  private QuestionRepository questionRepository;

  private MockUserClass user1;

  private MockUserClass user2;

  private Progress progress;

  public Topic getPersistentTopic() {
    Iterable<Topic> allTopics = topicRepository.findAll();
    return allTopics.iterator().next();
  }

  public GameMap getPersistentGameMap() {
    Iterable<GameMap> allGameMaps = gameMapRepository.findAll();
    return allGameMaps.iterator().next();
  }

  public User getPersistentUser() {
    Iterable<User> allUsers = userRepository.findAll();
    return allUsers.iterator().next();
  }

  public Progress getPersistentProgress() {
    Iterable<Progress> allProgress = progressRepository.findAll();
    return allProgress.iterator().next();
  }

  public Question getPersistentQuestion() {
    Iterable<Question> allQuestions = questionRepository.findAll();
    return allQuestions.iterator().next();
  }

  @BeforeEach
  public void setUp() {
    this.user1 = new MockUserClass();
    this.user1.setEmail("student2@test.com");
    this.user1.setName("student2");
    this.user1.setRole("ROLE_STUDENT");
    this.user1.setPass("pass2");

    this.user2 = new MockUserClass();
    this.user2.setEmail("student3@test.com");
    this.user2.setName("student3");
    this.user2.setRole("ROLE_STUDENT");
    this.user2.setPass("pass3");

    Coordinates coordinates = new Coordinates();
    coordinates.setX(1);
    coordinates.setY(1);

    this.progress = new Progress();
    this.progress.setPosition(coordinates);
    this.progress.setTimeTaken(0);
  }

  @Test
  @Order(-3)
  @WithUserDetails("admin1@test.com")
  public void createContext1() throws Exception {

    String user1Json = new ObjectMapper().writeValueAsString(this.user1);
    mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(user1Json));
  }

  @Test
  @Order(-2)
  @WithUserDetails("admin1@test.com")
  public void createContext2() throws Exception {

    String user2Json = new ObjectMapper().writeValueAsString(this.user2);
    mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(user2Json));
  }

  @Test
  @Order(-1)
  @WithUserDetails("teacher1@test.com")
  public void createContext3() throws Exception {
    Topic topic = new Topic();
    topic.setTitle("[Topic Title]: Multiplication");
    topic.setDescription("[Topic Description]: This topic is about simple multiplication");

    // We want principal information to be saved, do not use TopicRepository directly
    String topicJson = new ObjectMapper().writeValueAsString(topic);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/topics/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(topicJson));

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
        .andExpect(status().isOk());

    // Initialise Coordinates for question
    Coordinates coordinates = new Coordinates();
    coordinates.setX(2);
    coordinates.setY(2);

    // Initialise Question Map for Question
    Map<Integer, String> questionMap = new HashMap<>();
    questionMap.put(1, "Option 1");
    questionMap.put(2, "Option 2");
    questionMap.put(3, "Option 3");
    questionMap.put(4, "Option 4");

    // Create a Question
    Question question = new Question();
    question.setAnswer(1);
    question.setCoordinates(coordinates);
    question.setQuestionText("This is a question");
    question.setOptions(questionMap);

    String questionJson = new ObjectMapper().writeValueAsString(question);
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/gameMaps/%s/questions/create", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(questionJson))
        .andExpect(status().isOk());
  }

  @Order(1)
  @Test
  public void should_allowCreateProgress_ifAuthorized() throws Exception {
    // ---------------- For User 1 --------------------//
    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    String progress1Json = new ObjectMapper().writeValueAsString(this.progress);
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/progress/users/%s/gameMaps/%s",
                this.user1.getEmail(), getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accessToken)
            .content(progress1Json))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(2)
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

  @Order(3)
  @Test
  @Transactional
  public void should_allowFetchProgressByUserEmail_ifAuthorized() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s", this.user1.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].position[\"x\"]", is(this.progress.getPosition().getX())))
        .andExpect(jsonPath("$.content[0].position[\"y\"]", is(this.progress.getPosition().getY())))
        .andExpect(jsonPath("$.content[0].user.email", is(this.user1.getEmail())))
        .andExpect(jsonPath("$.content[0].user.role", is(this.user1.getRole())))
        .andExpect(jsonPath("$.content[0].user.name", is(this.user1.getName())))
        .andExpect(
            jsonPath("$.content[0].gameMap", is(getPersistentProgress().getGameMap().getId())))
        .andExpect(jsonPath("$.content[0].timeTaken", is(getPersistentProgress().getTimeTaken())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(4)
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

  @Order(5)
  @Test
  @Transactional
  public void should_allowFetchProgressByGameMapId_ifAuthorized() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/gameMaps/%s", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].position[\"x\"]", is(this.progress.getPosition().getX())))
        .andExpect(jsonPath("$.content[0].position[\"y\"]", is(this.progress.getPosition().getY())))
        .andExpect(jsonPath("$.content[0].user.email", is(this.user1.getEmail())))
        .andExpect(jsonPath("$.content[0].user.role", is(this.user1.getRole())))
        .andExpect(jsonPath("$.content[0].user.name", is(this.user1.getName())))
        .andExpect(
            jsonPath("$.content[0].gameMap", is(getPersistentProgress().getGameMap().getId())))
        .andExpect(jsonPath("$.content[0].timeTaken", is(getPersistentProgress().getTimeTaken())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }
  
  @Order(6)
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

  @Order(7)
  @Test
  @Transactional
  public void should_allowFetchProgressByUserEmailAndGameMapId_ifAuthorized() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s/gameMaps/%s",
                this.user1.getEmail(), getPersistentGameMap().getId()))
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.position[\"x\"]", is(this.progress.getPosition().getX())))
        .andExpect(jsonPath("$.position[\"y\"]", is(this.progress.getPosition().getY())))
        .andExpect(jsonPath("$.user.email", is(this.user1.getEmail())))
        .andExpect(jsonPath("$.user.role", is(this.user1.getRole())))
        .andExpect(jsonPath("$.user.name", is(this.user1.getName())))
        .andExpect(jsonPath("$.gameMap", is(getPersistentProgress().getGameMap().getId())))
        .andExpect(jsonPath("$.timeTaken", is(getPersistentProgress().getTimeTaken())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(8)
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

  @Order(9)
  @Test
  @Transactional
  public void should_rejectFetchProgressByUserEmailAndGameMapId_ifGameMapNotExists()
      throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s/gameMaps/%s",
                this.user1.getEmail(), getPersistentGameMap().getId() - 1))
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(10)
  @Test
  @Transactional
  public void should_rejectFetchProgressByUserEmailAndGameMapId_ifUserEmailNotExists()
      throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/users/%s/gameMaps/%s",
                this.user1.getEmail(), getPersistentGameMap().getId() - 1))
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(11)
  @Test
  @WithUserDetails("user1@test.com")
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
  }

  @Order(12)
  @Test
  @Transactional
  public void should_allowUpdateProgress_ifAuthorized() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    this.progress.setTimeTaken(10.0);

    String progressJson = new ObjectMapper().writeValueAsString(this.progress);
    mockMvc.perform(
        MockMvcRequestBuilders
            .put(String.format("/progress/users/%s/gameMaps/%s",
                this.user1.getEmail(), getPersistentGameMap().getId()))
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(progressJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(13)
  @Test
  @Transactional
  public void should_allowRequestForLeaderboard_ifAuthorized() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/gameMaps/%s/leaderboard",
                getPersistentGameMap().getId()))
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));


  }

  @Order(14)
  @Test
  @Transactional
  @WithUserDetails("user1@test.com")
  public void should_rejectRequestForLeaderboard_ifNotAuthorized() throws Exception {

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/gameMaps/%s/leaderboard",
                getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(15)
  @Test
  @Transactional
  @WithUserDetails("teacher1@test.com")
  public void should_allowRequestForAttemptCount_ifAuthorized() throws Exception {

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/gameMaps/%s/report",
                getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(16)
  @Test
  @Transactional
  @WithUserDetails("user1@test.com")
  public void should_rejectRequestForAttemptCount_ifNotAuthorized() throws Exception {

    mockMvc.perform(
        MockMvcRequestBuilders
            .get(String.format("/progress/gameMaps/%s/report",
                getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(17)
  @Test
  @Transactional
  @WithUserDetails("user1@test.com")
  public void should_rejectSubmittedAnswer_ifNotAuthorized() throws Exception {

    Integer answer = 3;
    String answerJson = new ObjectMapper().writeValueAsString(answer);

    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/progress/users/%s/gameMaps/%s/questions/%s/submit",
                "user1@test.com", getPersistentGameMap().getId(), getPersistentQuestion().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(answerJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(18)
  @Test
  @Transactional
  public void should_rejectSubmittedAnswer_ifNotSelf() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user2.getEmail())
        .param("password", this.user2.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    Integer answer = 3;
    String answerJson = new ObjectMapper().writeValueAsString(answer);

    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/progress/users/%s/gameMaps/%s/questions/%s/submit",
                this.user1.getEmail(), getPersistentGameMap().getId(),
                getPersistentQuestion().getId()))
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(answerJson))
        .andExpect(status().isUnauthorized())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(19)
  @Test
  @Transactional
  public void should_allowSubmitAnswer_ifAuthorizedAndSelf() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    Integer answer = 3;
    String answerJson = new ObjectMapper().writeValueAsString(answer);

    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/progress/users/%s/gameMaps/%s/questions/%s/submit",
                this.user1.getEmail(), getPersistentGameMap().getId(), getPersistentQuestion().getId()))
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(answerJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }
  
  @Order(20)
  @WithUserDetails("user1@test.com")
  @Test
  public void should_rejectDeleteProgress_ifNotAuthorized() throws Exception {

    mockMvc.perform(
        MockMvcRequestBuilders
            .delete(String.format("/progress/users/%s/gameMaps/%s",
                "user1@test.com", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }
  
  @Order(21)
  @Test
  public void should_rejectDeleteProgress_ifNotSelf() throws Exception {
  
  MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(
        MockMvcRequestBuilders
            .delete(String.format("/progress/users/%s/gameMaps/%s",
                this.user2.getEmail(), getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorizaton", "Bearer" + accessToken))
        .andExpect(status().isUnauthorized())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }
  
  @Order(22)
  @Test
  public void should_allowDeleteProgress_ifAuthorizedAndSelf() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user1.getEmail())
        .param("password", this.user1.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(
        MockMvcRequestBuilders
            .delete(String.format("/progress/users/%s/gameMaps/%s",
                this.user1.getEmail(), getPersistentGameMap().getId()))
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
    
    

    mockMvc.perform(delete("/oauth/revoke")
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer" + accessToken))
        .andExpect(status().isOk());
  }

  @Order(9997)
  @WithUserDetails("teacher1@test.com")
  @Test
  public void cleanupContext1() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/gameMaps/%s/questions/%s",
        getPersistentGameMap().getId(), getPersistentQuestion().getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    mockMvc.perform(MockMvcRequestBuilders.delete(String
        .format("/topics/%s/gameMaps/%s", getPersistentTopic().getId(),
            getPersistentGameMap().getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    mockMvc.perform(MockMvcRequestBuilders.delete("/topics/" + getPersistentTopic().getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Order(9998)
  @Test
  @WithUserDetails("admin1@test.com")
  public void cleanupContext2() throws Exception {

    mockMvc.perform(
        MockMvcRequestBuilders.delete(String.format("/users/%s", this.user1.getEmail()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Order(9999)
  @Test
  @WithUserDetails("admin1@test.com")
  public void cleanupContext3() throws Exception {

    mockMvc.perform(
        MockMvcRequestBuilders.delete(String.format("/users/%s", this.user2.getEmail()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

}


