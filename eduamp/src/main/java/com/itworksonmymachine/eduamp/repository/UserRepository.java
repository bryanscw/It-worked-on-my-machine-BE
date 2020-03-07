package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findUserByEmail(String email);

  boolean deleteUserByEmail(String email);

  boolean existsUserByEmail(String email);

}