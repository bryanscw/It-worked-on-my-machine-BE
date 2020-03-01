package com.itworksonmymachine.eduamp.unit.examples;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.itworksonmymachine.eduamp.config.TestConfig;
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
public class HelloMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithUserDetails("student1@test.com")
  public void should_allowUser_ifAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/hello?name=Seb")
        .accept(MediaType.ALL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.greetings", is("Welcome Seb (student1@test.com)!")))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @WithUserDetails("user1@test.com")
  public void should_rejectUser_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/hello?name=Seb")
        .accept(MediaType.ALL))
        .andExpect(status().isForbidden())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  public void should_reject_ifNotAuthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/hello?name=Seb")
        .accept(MediaType.ALL))
        .andExpect(status().isUnauthorized())
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

}