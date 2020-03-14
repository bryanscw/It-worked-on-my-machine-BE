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

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestConfig.class
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private MockUserClass user;

  @BeforeEach
  private void setup() {
    this.user = new MockUserClass();
    this.user.setEmail("create-student@test.com");
    this.user.setPass("password");
    this.user.setName("name");
    this.user.setRole("ROLE_STUDENT");
  }

  @Test
  @WithUserDetails("teacher1@test.com")
  public void should_rejectCreate_ifNotAuthorized() throws Exception {
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    this.mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_allowCreate_ifAuthorized() throws Exception {
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    this.mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", is("create-student@test.com")))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_rejectCreate_ifUserAlreadyExists() throws Exception {
    String userJson = new ObjectMapper().writeValueAsString(this.user);

    // Create a user
    this.mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk());

    // Add a user that already exists
    this.mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isBadRequest())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

}
