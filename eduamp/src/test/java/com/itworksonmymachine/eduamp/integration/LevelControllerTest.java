package com.itworksonmymachine.eduamp.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itworksonmymachine.eduamp.config.TestConfig;
import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.entity.Level;
import com.itworksonmymachine.eduamp.entity.Topic;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.LevelRepository;
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
public class LevelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private LevelRepository levelRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private GameMapRepository gameMapRepository;

  private Level level;

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

  @BeforeEach
  private void setup() throws Exception {
    this.level = new Level();
    this.level.setGameMap(new GameMap());
    this.level.setPlayable(false);
    this.level.setTopic(new Topic());
  }

  @Test
  @Order(1)
  @WithUserDetails("teacher1@test.com")
  public void should_allowCreateLevel_ifAuthorized() throws Exception {
    Topic topic = new Topic();
    topic.setTitle("[Topic Title]: Multiplication");
    topic.setDescription("[Topic Description]: This topic is about simple multiplication");

    String topicJson = new ObjectMapper().writeValueAsString(topic);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/topics/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(topicJson))
        .andExpect(status().isOk()).andReturn();

    String levelJson = new ObjectMapper().writeValueAsString(this.level);
    mockMvc.perform(
        MockMvcRequestBuilders
            .post(String.format("/topics/%s/levels/create", getPersistentTopic().getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(levelJson))
        .andExpect(status().isOk());
  }

  @Test
  @Order(2)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void should_rejectCreateLevel_ifNotAuthorized() throws Exception {
    Topic topic = getPersistentTopic();
    this.level.setTopic(topic);

    String levelJson = new ObjectMapper().writeValueAsString(this.level);
    mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/topics/%s/levels/create", topic.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(levelJson))
        .andExpect(status().isForbidden());
  }

}