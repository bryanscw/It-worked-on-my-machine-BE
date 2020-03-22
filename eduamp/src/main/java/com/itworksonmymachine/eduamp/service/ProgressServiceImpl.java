package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.entity.GameMap;
import com.itworksonmymachine.eduamp.entity.Progress;
import com.itworksonmymachine.eduamp.entity.QuestionProgress;
import com.itworksonmymachine.eduamp.entity.User;
import com.itworksonmymachine.eduamp.exception.NotAuthorizedException;
import com.itworksonmymachine.eduamp.exception.ResourceAlreadyExistsException;
import com.itworksonmymachine.eduamp.exception.ResourceNotFoundException;
import com.itworksonmymachine.eduamp.repository.GameMapRepository;
import com.itworksonmymachine.eduamp.repository.ProgressRepository;
import com.itworksonmymachine.eduamp.repository.QuestionProgressRepository;
import com.itworksonmymachine.eduamp.repository.UserRepository;
import java.util.Collection;
import java.util.Optional;
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

  private final QuestionProgressRepository questionProgressRepository;

  private final UserRepository userRepository;

  private final GameMapRepository gameMapRepository;

  public ProgressServiceImpl(ProgressRepository progressRepository,
      QuestionProgressRepository questionProgressRepository, UserRepository userRepository,
      GameMapRepository gameMapRepository) {
    this.progressRepository = progressRepository;
    this.questionProgressRepository = questionProgressRepository;
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
    String principalName = ((org.springframework.security.core.userdetails.User) authentication
        .getPrincipal()).getUsername();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
      return progressRepository
          .findProgressByUser_EmailAndMap_Id(principalName, gameMapId, pageable);
    } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))
        || authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      // Check if gameMap exists
      gameMapRepository.findById(gameMapId).orElseThrow(() -> {
        String gameMapNotFoundMsg = String.format("GameMap with gameMapId: [%s] not found", gameMapId);
        log.error(gameMapNotFoundMsg);
        return new ResourceNotFoundException(gameMapNotFoundMsg);
      });
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
    String principalName = ((org.springframework.security.core.userdetails.User) authentication
        .getPrincipal()).getUsername();
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
    String principalName = ((org.springframework.security.core.userdetails.User) authentication
        .getPrincipal()).getUsername();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    Progress progress = progressRepository
        .findProgressByUser_EmailAndMap_Id(userEmail, gameMapId)
        .orElseThrow(() -> new ResourceNotFoundException(errorMsg));

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

  /**
   * Create a Progress of a User in a specific GameMap.
   *
   * <p>
   * All users are only allowed to add a Progress for their own account. i.e. They are not able to
   * add Progress for another User.
   *
   * @param userEmail      Email of User
   * @param gameMapId      GameMap id
   * @param progress       Progress to be created
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Created Progress
   */
  @Override
  public Progress createProgress(String userEmail, Integer gameMapId, Progress progress,
      Authentication authentication) {
    String principalName = ((org.springframework.security.core.userdetails.User) authentication
        .getPrincipal()).getUsername();
    // Ensure that user is only creating a  Progress for themselves
    if (!userEmail.equals(principalName)) {
      String notAuthMsg = String
          .format("[%s] is not allowed to create a Progress for [%s]", principalName, userEmail);
      log.error(notAuthMsg);
      throw new NotAuthorizedException(notAuthMsg);
    }

    // Find the referenced User and GameMap
    User userToFind = userRepository.findUserByEmail(userEmail).orElseThrow(() -> {
      String errorMsg = String.format("User with email [%s] not found", userEmail);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });
    GameMap gameMapToFind = gameMapRepository.findById(gameMapId).orElseThrow(() -> {
      String errorMsg = String.format("GameMap with gameMapId [%s] not found", gameMapId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });

    Optional<Progress> progressToFind = progressRepository
        .findProgressByUser_EmailAndMap_Id(userEmail, gameMapId);

    // Only able to create QuestionProgress for questions available in the GameMap
    for (QuestionProgress questionProgress : progress.getQuestionProgressList()) {
      if (!gameMapToFind.getQuestions().contains(questionProgress.getQuestion())) {
        String notFoundMsg = String
            .format("Unable to create QuestionProgress for Question: [%s] in GameMap [%s]",
                questionProgress.getQuestion().toString(), gameMapToFind.toString());
        throw new ResourceNotFoundException(notFoundMsg);
      }
    }

    if (progressToFind.isEmpty()) {
      // Progress not found, should create
      progress.setUser(userToFind);
      progress.setMap(gameMapToFind);
      return progressRepository.save(progress);
    } else {
      // Progress found, throw ResourceAlreadyExistsException
      String alreadyExistMsg = String
          .format("Progress for User: [%s] and GameMap: [%s] already exists", userEmail, gameMapId);
      log.error(alreadyExistMsg);
      throw new ResourceAlreadyExistsException(alreadyExistMsg);
    }
  }

  /**
   * Update a Progress of a user in a specific GameMap.
   * <p>
   * All users are only allowed to modify the Progress for their own account. i.e. They are not able
   * to modify Progress for another User.
   *
   * @param userEmail      Email of User
   * @param gameMapId      GameMap id
   * @param progress       Progress to be created
   * @param authentication Authentication context containing information of the user submitting the
   *                       request
   * @return Updated Progress
   */
  @Override
  public Progress updateProgress(String userEmail, Integer gameMapId, Progress progress,
      Authentication authentication) {
    String principalName = ((org.springframework.security.core.userdetails.User) authentication
        .getPrincipal()).getUsername();
    // Ensure that user is only modifying the Progress for themselves
    if (!userEmail.equals(principalName)) {
      String notAuthMsg = String
          .format("[%s] is not allowed to modify the Progress for [%s]", principalName, userEmail);
      log.error(notAuthMsg);
      throw new NotAuthorizedException(notAuthMsg);
    }

    // Find the referenced User, GameMap and Progress
    User userToFind = userRepository.findUserByEmail(userEmail).orElseThrow(() -> {
      String errorMsg = String.format("User with email [%s] not found", userEmail);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });
    GameMap gameMapToFind = gameMapRepository.findById(gameMapId).orElseThrow(() -> {
      String errorMsg = String.format("GameMap with gameMapId [%s] not found", gameMapId);
      log.error(errorMsg);
      return new ResourceNotFoundException(errorMsg);
    });
    Progress progressToFind = progressRepository
        .findProgressByUser_EmailAndMap_Id(userEmail, gameMapId).orElseThrow(() -> {
          String errorMsg = String
              .format("Progress referenced by User: [%s] and GameMap: [%s] not found", userEmail,
                  gameMapId);
          log.error(errorMsg);
          return new ResourceNotFoundException(errorMsg);
        });

    // Prevent cheating when user tries to decrement the time taken
    if (progress.getTimeTaken() == -1.0 || progress.getTimeTaken() < progressToFind
        .getTimeTaken()) {
      throw new NotAuthorizedException("Not authorized to create a new Progress");
    }

    // TODO: Prevent cheating when user tries to mutate questions attempted in regressive manner

    progress.setId(progressToFind.getId());
    progress.setMap(gameMapToFind);
    progress.setUser(userToFind);
    progressToFind.setTimeTaken(progress.getTimeTaken());
    progressToFind.setComplete(progress.isComplete());

    if (progress.getPosition() != null) {
      progressToFind.setPosition(progress.getPosition());
    }

    if (!progress.getQuestionProgressList().isEmpty()
        || progress.getQuestionProgressList() != null) {
      progressToFind.setQuestionProgressList(progress.getQuestionProgressList());
    }

    // Save the progress
    return progressRepository.save(progress);
  }

}
