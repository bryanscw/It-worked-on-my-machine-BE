package com.itworksonmymachine.eduamp.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "level")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Level extends Auditable<String> {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Getter
  @Setter
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "topic_id")
  private Topic topic;

  @Getter
  @Setter
//  @OneToMany(mappedBy = "level", cascade = CascadeType.ALL)
  private List<LearningMaterial> learningMaterials;

  @Getter
  @Setter
//  @OneToMany(mappedBy = "level", cascade = CascadeType.ALL)
  private List<Question> questions;

  @Getter
  @Setter
  @OneToOne
  private GameMap map;

  @Getter
  @Setter
  private boolean isPlayable;

}