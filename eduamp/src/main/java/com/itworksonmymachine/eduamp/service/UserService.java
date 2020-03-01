package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.User;
import java.util.List;

public interface UserService {

  public User create(User appUser);

  public User save(User appUser);

  public boolean delete(String email);

  public User get(String email);

  public List<User> getAll();

}
