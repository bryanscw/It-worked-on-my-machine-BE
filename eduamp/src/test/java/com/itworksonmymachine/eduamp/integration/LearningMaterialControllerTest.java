package com.itworksonmymachine.eduamp.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.itworksonmymachine.eduamp.config.TestConfig;
import com.itworksonmymachine.eduamp.entity.LearningMaterial;
import com.itworksonmymachine.eduamp.entity.Topic;
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
  private LearningMaterialRepository learningMaterialRepository;

  @Autowired
  private TopicRepository topicRepository;

  private LearningMaterial learningMaterial;

  private Topic topic;

  private LearningMaterial getPersistentLearningMaterial() {
    Iterable<LearningMaterial> allLearningMaterials = learningMaterialRepository.findAll();
    return allLearningMaterials.iterator().next();
  }

  private Topic getPersistentTopic(){
    Iterable<Topic> allTopics = topicRepository.findAll();
    return allTopics.iterator().next();
  }

  @BeforeEach
  private void setUp(){
    this.learningMaterial = new LearningMaterial();
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

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/learningMaterial")
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
        MockMvcRequestBuilders.post("/learningMaterial")
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
        MockMvcRequestBuilders.post("/learningMaterial")
            .contentType(MediaType.APPLICATION_JSON)
            .content(learningMaterialJson))
        .andExpect(status().isBadRequest())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(4)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_allowFetchLearningMaterial_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/learningMaterial")
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
  @Order(5)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectUpdateLearningMaterial_ifNotAuthorized() throws Exception {
    this.learningMaterial.setTitle("New title");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(MockMvcRequestBuilders.put("/learningMaterial")
        .contentType(MediaType.APPLICATION_JSON)
        .content(learningMaterialJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(6)
  @WithUserDetails("teacher2@test.com")
  @Transactional
  public void should_rejectUpdateLearningMaterial_ifNotOwner() throws Exception {
    this.learningMaterial.setTitle("New title");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(MockMvcRequestBuilders.put("/learningMaterial")
        .contentType(MediaType.APPLICATION_JSON)
        .content(learningMaterialJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(7)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowUpdateLearningMaterial_ifAuthorized() throws Exception {
    this.learningMaterial.setTitle("New title");

    String learningMaterialJson = new ObjectMapper().writeValueAsString(this.learningMaterial);
    mockMvc.perform(MockMvcRequestBuilders.put("/learningMaterial")
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
}

