package com.itworksonmymachine.eduamp.integration;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itworksonmymachine.eduamp.config.MockUserClass;
import com.itworksonmymachine.eduamp.config.TestConfig;
import com.jayway.jsonpath.JsonPath;
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
  public void should_allow_ifValidCredentials() throws Exception {
    // Create user
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk());

    // Perform login
    mockMvc.perform(
        MockMvcRequestBuilders.post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user.getEmail())
        .param("password", this.user.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token_type", is("bearer")))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  public void should_reject_ifInvalidCredentials() throws Exception {
    // Perform login
    mockMvc.perform(
        MockMvcRequestBuilders.post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", "invalid_account@test.com")
        .param("password", "invalid_password")
        .param("grant_type", "password"))
        .andExpect(status().isBadRequest())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_beDifferentToken_ifRefresh() throws Exception {

    // Create user
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk());

    MvcResult initialMvcResult = mockMvc.perform(
        MockMvcRequestBuilders.post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user.getEmail())
        .param("password", this.user.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String initialAuthToken = JsonPath
        .read(initialMvcResult.getResponse().getContentAsString(), "$.access_token");

    String refreshToken = JsonPath
        .read(initialMvcResult.getResponse().getContentAsString(), "$.refresh_token");

    MvcResult finalMvcResult = mockMvc.perform(
        MockMvcRequestBuilders.post(String.format("/oauth/token?grant_type=refresh_token&refresh_token=%s", refreshToken))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes())))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())))
        .andReturn();

    String refreshedAuthToken = JsonPath
        .read(finalMvcResult.getResponse().getContentAsString(), "$.access_token");

    assertThat(initialAuthToken, is(not(refreshedAuthToken)));
  }

  @Test
  @WithUserDetails("admin1@test.com")
  @Transactional
  public void should_logout_ifValidSession() throws Exception {
    // Create user
    String userJson = new ObjectMapper().writeValueAsString(this.user);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk());

    // Perform login
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.post("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .header(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64Utils.encodeToString("my-client:my-secret".getBytes()))
        .param("username", this.user.getEmail())
        .param("password", this.user.getPass())
        .param("grant_type", "password"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token_type", is("bearer")))
        .andReturn();

    String accessToken = JsonPath
        .read(mvcResult.getResponse().getContentAsString(), "$.access_token");

    // Perform logout
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/oauth/revoke")
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  public void should_throwError_ifInvalidSession() throws Exception {
    // Perform logout
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/oauth/revoke")
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer invalidToken"))
        .andExpect(status().isUnauthorized())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

}

