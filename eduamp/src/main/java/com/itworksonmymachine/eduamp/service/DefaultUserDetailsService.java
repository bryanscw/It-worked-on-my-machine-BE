package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.AppUser;
import com.itworksonmymachine.eduamp.repository.AppUserRepository;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultUserDetailsService implements UserDetailsService {

  @Autowired
  private AppUserRepository appUserRepository;

  public DefaultUserDetailsService(AppUserRepository appUserRepository) {
    this.appUserRepository = appUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    final Optional<AppUser> userEntity = appUserRepository.findById(username);

    if (userEntity.isPresent()) {
      final AppUser appUser = userEntity.get();

      return new User(appUser.getUserEmail(),
          appUser.getUserPass(),
          Collections.singletonList(new SimpleGrantedAuthority(appUser.getUserRole())));
    }

    return null;
  }

}