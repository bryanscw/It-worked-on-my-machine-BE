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
import com.itworksonmymachine.eduamp.entity.Level;
import com.itworksonmymachine.eduamp.entity.Topic;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestConfig.class
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class TopicControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private Topic topic;

  @BeforeEach
  private void setup() {
    Level level = new Level();
    ArrayList<Level> levelArrayList = new ArrayList<>();
    levelArrayList.add(level);

    this.topic = new Topic();
    this.topic.setTitle("[Topic Title]: Multiplication");
    this.topic.setDescription("[Topic Description]: This topic is about simple multiplication");
  }

  @Test
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_createTopic_ifAuthorized() throws Exception {
    String topicJson = new ObjectMapper().writeValueAsString(this.topic);
    mockMvc.perform(MockMvcRequestBuilders.post("/topic/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(topicJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(this.topic.getTitle())))
        .andExpect(jsonPath("$.description", is(this.topic.getDescription())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

}
