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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestConfig.class
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class OAuthTest {

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
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void shouldLogin() throws Exception {
    // Create user
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    this.mockMvc.perform(post("/admin/user/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk());

    // Perform login
    this.mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", "create-student@test.com")
        .param("password", "password")
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token_type", is("bearer")))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

}
