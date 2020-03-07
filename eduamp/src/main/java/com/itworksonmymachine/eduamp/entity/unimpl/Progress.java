package com.itworksonmymachine.eduamp.entity.unimpl;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Progress {

  @Getter
  @Setter
  public double timeTaken;

  @Getter
  @Setter
  public int studentID;

  @Getter
  @Setter
  public int[] position;

  @Getter
  @Setter
  public boolean isComplete;

  @Getter
  @Setter
  public int mapID;

  @Getter
  @Setter
  public List<QuestionProgress> questionAttempts;

}