package com.itworksonmymachine.eduamp.config;

import java.util.Collections;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestConfig {

  @Bean
  @Primary
  public UserDetailsService userDetailsService() {

    // Creating a user with the no roles
    User basicUser = new org.springframework.security.core.userdetails.User(
        "user1@test.com",
        "password",
        Collections.emptyList());

    // Creating a user with the STUDENT role
    User student = new org.springframework.security.core.userdetails.User(
        "student1@test.com",
        "password",
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")));

    // Creating a user with the ADMIN role
    User admin = new org.springframework.security.core.userdetails.User(
        "admin1@test.com",
        "password",
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

    // Creating a user with the TEACHER role
    User teacher = new org.springframework.security.core.userdetails.User(
        "teacher1@test.com",
        "password",
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER")));

    // Creating a user with the TEACHER role
    User teacher2 = new org.springframework.security.core.userdetails.User(
        "teacher2@test.com",
        "password",
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER")));

    return new InMemoryUserDetailsManager(basicUser, student, admin, teacher, teacher2);
  }

}