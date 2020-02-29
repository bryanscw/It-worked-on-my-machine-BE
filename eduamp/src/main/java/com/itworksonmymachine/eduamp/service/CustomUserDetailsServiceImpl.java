package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.model.User;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Slf4j
@Service("userDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User userFromDataBase = userRepository.findOneByUsername(username);
    if (userFromDataBase == null) {
      log.info("User [{}] was not found in the database", username);
      throw new UsernameNotFoundException("User " + username + " was not found in the database");
    }
    return userFromDataBase;

  }
}
