package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  public User create(User user);

  public User updateUser(User user);

  public int delete(String email);

  public User get(String email);

  public Page<User> fetchAll(Pageable pageable);

}
