package com.itworksonmymachine.eduamp.model.dto;

import com.itworksonmymachine.eduamp.entity.Question;
import com.itworksonmymachine.eduamp.entity.User;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionAttemptDTO {

  @Getter
  @Setter
  private Question question;

  @Getter
  @Setter
  private ArrayList<Attempt> attempts = new ArrayList<>();

  public void addAttempt(User user, int attemptCount) {
    this.attempts.add(new Attempt(user, attemptCount));
  }

}


@AllArgsConstructor
@NoArgsConstructor
class Attempt {

  @Getter
  @Setter
  private User user;

  @Getter
  @Setter
  private int attemptCount;

}