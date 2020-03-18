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
import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import javax.transaction.Transactional;
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
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  private MockUserClass user;

  public User getPersistentUser() {
    Iterable<User> allUsers = userRepository.findAll();
    return allUsers.iterator().next();
  }

  @BeforeEach
  private void setup() {
    this.user = new MockUserClass();
    this.user.setEmail("create-student@test.com");
    this.user.setPass("password");
    this.user.setName("name");
    this.user.setRole("ROLE_STUDENT");
  }

  @Order(1)
  @Test
  @WithUserDetails("teacher1@test.com")
  public void should_rejectCreate_ifNotAuthorized() throws Exception {
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(2)
  @Test
  @WithUserDetails("admin1@test.com")
  public void should_allowCreate_ifAuthorized() throws Exception {
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", is("create-student@test.com")))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(3)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_rejectCreate_ifUserAlreadyExists() throws Exception {
    String userJson = new ObjectMapper().writeValueAsString(this.user);

    // Add a user that already exists
    mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isBadRequest())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(4)
  @Test
  @Transactional
  public void should_allowFetchUser_ifAuthorized() throws Exception {

    MvcResult mvcResult = this.mockMvc.perform(post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user.getEmail())
        .param("password", this.user.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    mockMvc.perform(post("/users/me")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", is(getPersistentUser().getEmail())))
        .andExpect(jsonPath("$.name", is(getPersistentUser().getName())))
        .andExpect(jsonPath("$.role", is(getPersistentUser().getRole())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(5)
  @Test
  @WithUserDetails("user1@test.com")
  @Transactional
  public void should_rejectFetchUser_ifNotAuthorized() throws Exception {
    mockMvc.perform(post("/users/me")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(6)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_allowFetchUsers_ifAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.get("/users/")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].email", is(getPersistentUser().getEmail())))
        .andExpect(jsonPath("$.content[0].name", is(getPersistentUser().getName())))
        .andExpect(jsonPath("$.content[0].role", is(getPersistentUser().getRole())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(7)
  @Test
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_rejectFetchUsers_ifNotAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.get("/users/")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(8)
  @Test
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_rejectUpdateUser_ifNotAuthorized() throws Exception {
    this.user.setEmail("newEmail@test.com");

    String userJson = new ObjectMapper().writeValueAsString(this.user);
    mockMvc.perform(
        MockMvcRequestBuilders.put(String.format("/users/%s", getPersistentUser().getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(9)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_rejectUpdateUser_ifNotExists() throws Exception {
    this.user.setEmail("newEmail@test.com");

    String userJson = new ObjectMapper().writeValueAsString(this.user);
    mockMvc.perform(
        MockMvcRequestBuilders.put(String.format("/users/%s", "user1@test.com"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(10)
  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_allowUpdateUser_ifAuthorized() throws Exception {

    this.user.setName("new name");

    String userJson = new ObjectMapper().writeValueAsString(this.user);
    mockMvc.perform(
        MockMvcRequestBuilders.put(String.format("/users/%s", getPersistentUser().getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", is(getPersistentUser().getEmail())))
        .andExpect(jsonPath("$.name", is(getPersistentUser().getName())))
        .andExpect(jsonPath("$.role", is(getPersistentUser().getRole())))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(11)
  @Test
  @WithUserDetails("teacher1@test.com")
  @Transactional
  public void should_rejectDeleteUser_ifNotAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.delete(String.format("/users/%s", getPersistentUser().getEmail()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(12)
  @Test
  @WithUserDetails("admin1@test.com")
  public void should_allowDeleteUser_ifAuthorized() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.delete(String.format("/users/%s", this.user.getEmail()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Order(13)
  @Test
  @WithUserDetails("admin1@test.com")
  public void should_rejectDeleteUser_ifNotExists() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.delete(String.format("/users/%s", this.user.getEmail()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

}

