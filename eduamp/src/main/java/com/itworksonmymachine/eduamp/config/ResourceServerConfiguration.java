package com.itworksonmymachine.eduamp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Slf4j
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @Autowired
  private DefaultTokenServices defaultTokenServices;

  @Autowired
  private JwtTokenStore jwtTokenStore;

  @Value("${config.oauth2.publicKey}")
  private String publicKey;

  @Value("${config.oauth2.privateKey}")
  private String privateKey;

  @Value("${config.oauth2.resource.id}")
  private String resourceId;

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .anonymous().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS).permitAll()
        .antMatchers("/customer/**").authenticated();
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) {
    resources
        .resourceId(resourceId)
        .tokenServices(defaultTokenServices)
        .tokenStore(jwtTokenStore);
  }

}