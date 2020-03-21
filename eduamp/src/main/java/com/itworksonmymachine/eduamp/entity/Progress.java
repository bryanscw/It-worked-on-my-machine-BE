package com.itworksonmymachine.eduamp.entity;

import com.itworksonmymachine.eduamp.model.Coordinates;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "progress")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Progress extends Auditable<String> {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Getter
  @Setter
  @OneToOne
  private User user;

  @Getter
  @Setter
  @OneToOne(fetch= FetchType.EAGER)
  private GameMap map;

  @Getter
  @Setter
  private Coordinates position;

  @Getter
  @Setter
  private double timeTaken = -1;

  @Getter
  @Setter
  private boolean isComplete;

  @Getter
  @Setter
  @OneToMany(mappedBy = "progress", cascade = {CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
  private List<QuestionProgress> questionProgressList = new ArrayList<>();

}
