package com.itworksonmymachine.eduamp.entity;

import javax.persistence.CascadeType;
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
import org.codehaus.jackson.annotate.JsonBackReference;

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

  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "level_id", nullable = false)
  private Level level;

  @Getter
  @Setter
  private String title;

  @Getter
  @Setter
  private String link;

  @Getter
  @Setter
  private String description;

}