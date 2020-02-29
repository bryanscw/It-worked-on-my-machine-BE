package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.model.UserTokenSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserTokenSessionRepository extends CrudRepository<UserTokenSession, Long> {

  /**
   * Find {@link UserTokenSession} for the given username.
   *
   * @param username
   * @return @{@link UserTokenSession}
   */
  UserTokenSession findOneByUsername(String username);

}