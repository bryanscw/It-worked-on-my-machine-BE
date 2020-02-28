package com.itworksonmymachine.eduamp.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.itworksonmymachine.eduamp.EduampApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EduampApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthServerIntegrationTest {

  @Test
  public void whenLoadApplication_thenSuccess() {

  }
}