package com.itworksonmymachine.eduamp.unit.examples;


import com.itworksonmymachine.eduamp.model.Welcome;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void should_reject_ifNotAuthorized() {
    // Given
    String testName = "test";
    String request = "/api/hello?name=" + testName;

    // When
    ResponseEntity<Welcome> response = restTemplate.getForEntity(request, Welcome.class);

    // Then
    Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

}
