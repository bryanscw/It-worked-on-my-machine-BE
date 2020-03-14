package com.itworksonmymachine.eduamp.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.itworksonmymachine.eduamp.config.TestConfig;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
public class ProgressControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @Order(1)
  @WithUserDetails("student1@test.com")
  @Transactional
  public void testMethod() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/progress/users/1/gameMaps/2")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

}
