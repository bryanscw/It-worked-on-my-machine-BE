package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

  public Optional<User> findUserByEmail(String email);

  public boolean deleteUserByEmail(String email);

  public boolean existsUserByEmail(String email);

}