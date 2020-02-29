package com.itworksonmymachine.eduamp.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@ComponentScan("com.itworksonmymachine.eduamp")
@EntityScan("com.itworksonmymachine.eduamp.model")
@EnableJpaRepositories("com.itworksonmymachine.eduamp.repository")
public class PersistentContext {

}