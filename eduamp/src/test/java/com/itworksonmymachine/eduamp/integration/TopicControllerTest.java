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
import com.itworksonmymachine.eduamp.entity.Topic;
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
public class TopicControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TopicRepository topicRepository;

  private Topic topic;

  @BeforeEach
  private void setup() {
    this.topic = new Topic();
    this.topic.setTitle("[Topic Title]: Multiplication");
    this.topic.setDescription("[Topic Description]: This topic is about simple multiplication");
  }

  /**
   * Returns the id of the topic created on the test: should_allowCreateTopic_ifAuthorized.
   *
   * @return Persistent topic id.
   */
  private int getPersistentTopicId() {
    // Test db should only have 1 topic
    Iterable<Topic> allTopics = topicRepository.findAll();
    Topic persistentTopic = allTopics.iterator().next();
    return persistentTopic.getId();
  }

  @Test
  @Order(1)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectCreateTopic_ifNotAuthorized() throws Exception {
    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
    mockMvc.perform(MockMvcRequestBuilders.post("/topics/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(topicJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(2)
  @WithUserDetails("teacher1@test.com")
  public void should_allowCreateTopic_ifAuthorized() throws Exception {
    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
    mockMvc.perform(MockMvcRequestBuilders.post("/topics/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(topicJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(this.topic.getTitle())))
        .andExpect(jsonPath("$.description", is(this.topic.getDescription())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())))
        .andReturn();
  }

  @Test
  @Order(3)
  @WithUserDetails("user1@test.com")
  public void should_rejectFetchTopics_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/topics/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(4)
  @WithUserDetails("teacher1@test.com")
  public void should_allowFetchTopics_ifAuthorized() throws Exception {
    // There will only be 1 topic in the database
    mockMvc.perform(MockMvcRequestBuilders.get("/topics/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title", is(this.topic.getTitle())))
        .andExpect(jsonPath("$.content[0].description", is(this.topic.getDescription())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(5)
  @WithUserDetails("user1@test.com")
  public void should_rejectFetchTopic_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/topics/" + getPersistentTopicId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(6)
  @WithUserDetails("teacher1@test.com")
  public void should_allowFetchTopic_ifAuthorized() throws Exception {
    // There will only be 1 topic in the database
    mockMvc.perform(MockMvcRequestBuilders.get("/topics/" + getPersistentTopicId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(this.topic.getTitle())))
        .andExpect(jsonPath("$.description", is(this.topic.getDescription())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(7)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectUpdateTopic_ifNotAuthorized() throws Exception {
    // Update topic
    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
    mockMvc.perform(MockMvcRequestBuilders.put("/topics/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(topicJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

//  @Test
//  @Order(8)
//  @WithUserDetails("teacher2@test.com")
//  @Transactional
//  public void should_rejectUpdateTopic_ifNotOwner() throws Exception {
//    // Update the topic title
//    String newTitle = "This is a new title";
//    this.topic.setTitle(newTitle);
//
//    // Update topic with a different user
//    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
//    mockMvc.perform(MockMvcRequestBuilders.put("/topics/" + getPersistentTopicId())
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(topicJson))
//        .andExpect(status().isUnauthorized())
//        .andDo(document("{methodName}",
//            preprocessRequest(prettyPrint()),
//            preprocessResponse(prettyPrint())));
//  }

  @Test
  @Order(9)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowUpdateTopic_ifAuthorized() throws Exception {
    // Update the topic title
    String newTitle = "This is a new title";
    this.topic.setTitle(newTitle);

    // Update topic
    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
    mockMvc.perform(MockMvcRequestBuilders.put("/topics/" + getPersistentTopicId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(topicJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(newTitle)))
        .andExpect(jsonPath("$.description", is(this.topic.getDescription())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(10)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectPatchTopic_ifNotAuthorized() throws Exception {
    // Update topic
    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
    mockMvc.perform(MockMvcRequestBuilders.patch("/topics/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(topicJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

//  @Test
//  @Order(11)
//  @WithUserDetails("teacher2@test.com")
//  @Transactional
//  public void should_rejectPatchTopic_ifNotOwner() throws Exception {
//    // Update the topic title
//    String newTitle = "This is a new title";
//    this.topic.setTitle(newTitle);
//
//    // Update topic with a different user
//    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
//    mockMvc.perform(MockMvcRequestBuilders.patch("/topics/" + getPersistentTopicId())
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(topicJson))
//        .andExpect(status().isUnauthorized())
//        .andDo(document("{methodName}",
//            preprocessRequest(prettyPrint()),
//            preprocessResponse(prettyPrint())));
//  }

  @Test
  @Order(12)
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_allowPatchTopic_ifAuthorized() throws Exception {
    // Update the topic title
    String newTitle = "This is a new title";
    this.topic.setTitle(newTitle);

    // Update topic
    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
    mockMvc.perform(MockMvcRequestBuilders.patch("/topics/" + getPersistentTopicId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(topicJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(newTitle)))
        .andExpect(jsonPath("$.description", is(this.topic.getDescription())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @Order(13)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectDeleteTopic_ifNotAuthorized() throws Exception {
    // Delete topic
    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
    mockMvc.perform(MockMvcRequestBuilders.delete("/topics/" + getPersistentTopicId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(topicJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

//  @Test
//  @Order(14)
//  @WithUserDetails("teacher2@test.com")
//  @Transactional
//  public void should_rejectDeleteTopic_ifNotOwner() throws Exception {
//    // Delete topic
//    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
//    mockMvc.perform(MockMvcRequestBuilders.delete("/topics/" + getPersistentTopicId())
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(topicJson))
//        .andExpect(status().isUnauthorized())
//        .andDo(document("{methodName}",
//            preprocessRequest(prettyPrint()),
//            preprocessResponse(prettyPrint())));
//  }

//  @Test
//  @Order(15)
//  @WithUserDetails("teacher1@test.com")
//  public void should_allowDeleteTopic_ifAuthorizedAndOwner() throws Exception {
//    // Delete topic
//    mockMvc.perform(MockMvcRequestBuilders.delete("/topics/" + getPersistentTopicId())
//        .contentType(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk())
//        .andDo(document("{methodName}",
//            preprocessRequest(prettyPrint()),
//            preprocessResponse(prettyPrint())));
//  }

}
