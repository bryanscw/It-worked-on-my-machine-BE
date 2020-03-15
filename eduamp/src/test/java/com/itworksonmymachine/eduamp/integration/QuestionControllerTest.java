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
import com.itworksonmymachine.eduamp.entity.Question;
import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.model.Coordinates;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.LearningMaterialRepository;
import com.itworksonmymachine.eduamp.repository.TopicRepository;
import java.util.HashMap;
import java.util.Map;
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
public class QuestionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private GameMapRepository gameMapRepository;

  @Autowired
  private TopicRepository topicRepository;

  private Question question;

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

    // Initialise Game Map for question
    GameMap gameMap = new GameMap();
    gameMap.setMapDescriptor("This is a map descriptor");
    gameMap.setPlayable(false);
    
    // Initialise Coordinates for question
    Coordinates coordinates = new Coordinates();
    coordinates.setX(1);
    coordinates.setY(1);

    // Initialise Question Map for Question
    Map<Integer, String> questionMap = new HashMap<Integer, String>();
    questionMap.put(1, "Option 1");
    questionMap.put(2, "Option 2");
    questionMap.put(3, "Option 3");
    questionMap.put(4, "Option 4");

    // Create a Question
    this.question = new Question();
    this.question.setGameMap(gameMap);
    this.question.setAnswer(1);
    this.question.setCoordinates(coordinates);
    this.question.setQuestionText("This is a question");
    this.question.setOptions(questionMap);
  }

  @Test
  @Order(1)
  @WithUserDetails("teacher1@test.com")
  public void should_allowCreateQuestion_ifAuthorized() throws Exception {
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

    String gameMapJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(this.question.getGameMap());
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/topics/%s/gameMaps/create", getPersistentTopic().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(gameMapJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    String questionJson = new ObjectMapper().writeValueAsString(this.question);
    mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/gameMaps/%s/questions/create", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(questionJson))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(2)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectCreateQuestion_ifNotAuthorized() throws Exception {

    String questionJson = new ObjectMapper().writeValueAsString(this.question);
    mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/gameMaps/%s/question/create", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(questionJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(3)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_rejectCreateQuestion_ifAlreadyExist() throws Exception {
    String questionJson = new ObjectMapper().writeValueAsString(this.question);
    mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/gameMaps/%s/question/create", getPersistentGameMap().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(questionJson))
        .andExpect(status().isBadRequest())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(4)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_allowFetchQuestionTopic_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/gameMaps/gameMap/%s/questions/%s",
        getPersistentGameMap().getId(), this.question.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(getPersistentTopic().getId())))
        .andExpect(jsonPath("$.title", is(getPersistentTopic().getTitle())))
        .andExpect(jsonPath("$.description", is(getPersistentTopic().getDescription())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(5)
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchQuestionTopic_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/gameMaps/gameMap/%s/questions/%s",
        getPersistentGameMap().getId(), this.question.getId()))
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
  public void should_allowFetchQuestionsByGameMapId_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/gameMaps/%s/questions",
        getPersistentGameMap().getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.questionText", is(this.question.getQuestionText())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(7)
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchQuestionsByGameMapId_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(String.format("/gameMaps/%s/questions",
        getPersistentGameMap().getId()))
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
  public void should_rejectUpdateQuestion_ifNotAuthorized() throws Exception {
    this.question.setQuestionText("New question");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.question);
    mockMvc.perform(MockMvcRequestBuilders.put(String.format("/gameMaps/%s/questions/%s",
        getPersistentGameMap().getId(),
        this.question.getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(learningMaterialJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(9)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowUpdateQuestion_ifAuthorized() throws Exception {
    this.question.setQuestionText("New question");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.question);
    mockMvc.perform(MockMvcRequestBuilders.put(String.format("/gameMaps/%s/questions/%s",
        getPersistentGameMap().getId(),
        this.question.getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(learningMaterialJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.questionText", is(this.question.getQuestionText())))
        .andExpect(jsonPath("$.options", is(this.question.getOptions())))
        .andExpect(jsonPath("$.answer", is(this.question.getAnswer())))
        .andExpect(jsonPath("$.coordinates", is(this.question.getCoordinates())))
        .andExpect(jsonPath("$.gameMap", is(this.question.getGameMap())))
        .andExpect(jsonPath("$.id", is(this.question.getId())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(10)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectDeleteGameMapByQuestion_ifNotAuthorized() throws Exception {
    
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/gameMaps/%s/questions/%s",
        getPersistentGameMap().getId(),
        this.question.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(11)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_allowDeleteGameMapByQuestion_ifAuthorized() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/gameMaps/%s/questions/%s",
        getPersistentGameMap().getId(),
        this.question.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    // Delete Game Map
    gameMapRepository.deleteById(getPersistentGameMap().getId());
  }

  @Test
  @Order(12)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_rejectDeleteGameMapByQuestion_ifNotExist() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/gameMaps/%s/question/%s",
        getPersistentGameMap().getId(),
        this.question.getId()))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
    
    // Delete topic
    topicRepository.deleteById(getPersistentTopic().getId());
  }
}



