package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * @param username
   * @return @{@link User}
   */
  User findOneByUsername(String username);

}
