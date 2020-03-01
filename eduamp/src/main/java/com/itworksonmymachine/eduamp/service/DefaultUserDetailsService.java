package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;
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

    final Optional<User> userEntity = userRepository.findById(username);

    if (userEntity.isPresent()) {
      final User user = userEntity.get();

      return new org.springframework.security.core.userdetails.User(user.getEmail(),
          user.getPass(),
          Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }

    return null;
  }

}