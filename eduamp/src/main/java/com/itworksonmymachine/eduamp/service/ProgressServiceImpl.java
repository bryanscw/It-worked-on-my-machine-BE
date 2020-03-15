package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.Progress;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.ProgressRepository;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import java.security.Principal;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProgressServiceImpl implements ProgressService {

  private final ProgressRepository progressRepository;

  private final UserRepository userRepository;

  private final GameMapRepository gameMapRepository;

  public ProgressServiceImpl(ProgressRepository progressRepository, UserRepository userRepository,
      GameMapRepository gameMapRepository) {
    this.progressRepository = progressRepository;
    this.userRepository = userRepository;
    this.gameMapRepository = gameMapRepository;
  }

  /**
   * Fetch all Progress of Users of a certain GameMap.
   * <p>
   * Student should only be able to view their Progress.
   * <p>
   * Teacher and Admin can view anybody's Progress.
   *
   * @param pageable       Pagination context
   * @param gameMapId      GameMap id
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Progress of a certain GameMap referenced by gameMapId
   */
  @Override
  public Page<Progress> fetchAllProgressByGameMapId(Integer gameMapId,
      Authentication authentication, Pageable pageable) {
    String errorMsg = String
        .format("Not authorized to view Progress with gameMapId: [%s]", gameMapId);
    String principalName = ((Principal) authentication.getPrincipal()).getName();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
      return progressRepository
          .findProgressByUser_EmailAndMap_Id(principalName, gameMapId, pageable);
    } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))
        || authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      return progressRepository.findProgressByMap_Id(gameMapId, pageable);
    } else {
      throw new NotAuthorizedException(errorMsg);
    }
  }

  /**
   * Fetch all Progress of a certain user.
   * <p>
   * Student should only be able to view their Progress.
   * <p>
   * Teacher and Admin can view anybody's Progress.
   *
   * @param pageable       Pagination context
   * @param userEmail      Email of User
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Progress of a User referenced by User email.
   */
  @Override
  public Page<Progress> fetchAllProgressByUserEmail(String userEmail, Authentication authentication,
      Pageable pageable) {

    String errorMsg = String
        .format("Not authorized to view Progress for userEmail: [%s]", userEmail);
    String principalName = ((Principal) authentication.getPrincipal()).getName();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT")) && userEmail
        .equals(principalName)) {
      return progressRepository.findProgressByUser_Email(userEmail, pageable);
    } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))
        || authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      // The first if condition can actually be combined with this conditional block
      // For simplicity and ease of reading, it is separated
      return progressRepository.findProgressByUser_Email(userEmail, pageable);
    } else {
      throw new NotAuthorizedException(errorMsg);
    }
  }

  /**
   * Fetch a progress belonging to a User of a certain GameMap.
   * <p>
   * Student should only be able to view their Progress.
   * <p>
   * Teacher and Admin can view anybody's Progress.
   *
   * @param userEmail      User email
   * @param gameMapId      GameMap id
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Progress belonging of a User referenced by User email of a GameMap referenced by
   * GameMap id
   */
  @Override
  public Progress fetchProgressByUserEmailAndGameMapId(String userEmail, Integer gameMapId,
      Authentication authentication) {
    String errorMsg = String
        .format("Not authorized to view Progress of userEmail: [%s] with gameMapId: [%s]",
            userEmail, gameMapId);
    String principalName = ((Principal) authentication.getPrincipal()).getName();
    Progress progress = progressRepository
        .findProgressByUser_EmailAndMap_Id(userEmail, gameMapId);
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT")) && progress.getUser()
        .getEmail().equals(principalName)) {
      return progress;
    } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))
        || authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      return progress;
    } else {
      throw new NotAuthorizedException(errorMsg);
    }
  }

  @Override
  public Progress createProgress(String userEmail, Integer gameMapId, Progress progress,
      Authentication authentication) {
    return null;
  }

  @Override
  public Progress updateProgress(String userEmail, Integer gameMapId, Progress progress,
      Authentication authentication) {
    return null;
  }

}
