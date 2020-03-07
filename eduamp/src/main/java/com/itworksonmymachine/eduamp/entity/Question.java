package com.itworksonmymachine.eduamp.entity;

import java.util.Map;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Question {

  @Id
  @Getter
  @Setter
  public int questionID;

  @Getter
  @Setter
  public String questionText;

  @Getter
  @Setter
  public int answer;

  @Getter
  @Setter
  public Map<Integer, String> options;

}