package com.itworksonmymachine.eduamp.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "question_progress")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionProgress extends Auditable<String> {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne
  @Getter
  @Setter
  private Question question;


  @Getter
  @Setter
  private int attemptCount;

  @Getter
  @Setter
  private boolean isCleared;

}