package com.itworksonmymachine.eduamp.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itworksonmymachine.eduamp.config.MockUserClass;
import com.itworksonmymachine.eduamp.config.TestConfig;
import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import javax.transaction.Transactional;
import lombok.With;
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

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestConfig.class
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(OrderAnnotation.class)
public class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  private MockUserClass user;

  @BeforeEach
  private void setup() {
    this.user = new MockUserClass();
    this.user.setEmail("create-student@test.com");
    this.user.setPass("password");
    this.user.setName("name");
    this.user.setRole("ROLE_STUDENT");
  }

  public String getPersistentUserId(){
    Iterable<User> allUsers = userRepository.findAll();
    return allUsers.iterator().next().getEmail();
  }

  @Order(1)
  @Test
  @WithUserDetails("admin1@test.com")
  public void createUser() throws Exception {
    // Create user
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    this.mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk());
  }

  @Order(2)
  @Test
  public void should_beDifferentToken_ifRefresh() throws Exception {
    MvcResult initialMvcResult = mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user.getEmail())
        .param("password", this.user.getPass())
        .param("grant_type", "password"))
        .andReturn();

    String initialAuthToken = JsonPath
        .read(initialMvcResult.getResponse().getContentAsString(), "$.access_token");

    String refreshToken = JsonPath
        .read(initialMvcResult.getResponse().getContentAsString(), "$.refresh_token");

    MvcResult finalMvcResult = mockMvc.perform(
        post(String.format("/oauth/token?grant_type=refresh_token&refresh_token=%s", refreshToken))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes())))
        .andReturn();

    String refreshedAuthToken = JsonPath
        .read(finalMvcResult.getResponse().getContentAsString(), "$.access_token");

    assertThat(initialAuthToken, is(not(refreshedAuthToken)));
  }

  @Order(3)
  @Test
  @WithUserDetails("admin1@test.com")
  public void should_onlyHaveOneTokenRow_forOneUser() throws Exception {

    // Perform login
    this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/token/list")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    userRepository.deleteById(getPersistentUserId());
  }

}

