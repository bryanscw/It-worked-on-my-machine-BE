package com.itworksonmymachine.eduamp.unit.examples;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class HelloMockUserTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser(username = "user@test.com", roles = {"STUDENT"})
  public void should_allow_anyAuthenticatedUser() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/hello?name=Seb")
        .accept(MediaType.ALL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.greetings", is("Welcome Seb (user@test.com)!")));
  }

}
