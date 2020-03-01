package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  public DefaultUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final User userEntity = userRepository.findById(username)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("User [%s] not found", username)));

    return new org.springframework.security.core.userdetails.User(userEntity.getEmail(),
        userEntity.getPass(),
        Collections.singletonList(new SimpleGrantedAuthority(userEntity.getRole())));
  }

}