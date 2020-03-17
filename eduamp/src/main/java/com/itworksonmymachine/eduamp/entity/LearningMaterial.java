package com.itworksonmymachine.eduamp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "learning_material")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LearningMaterial extends Auditable<String> {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "game_map_id", nullable = false)
  @Getter
  @Setter
  @JsonBackReference
  private GameMap gameMap;

  @Getter
  @Setter
  private String title;

  @Getter
  @Setter
  private String link;

  @Getter
  @Setter
  @Column(columnDefinition = "TEXT")
  private String description;

}
