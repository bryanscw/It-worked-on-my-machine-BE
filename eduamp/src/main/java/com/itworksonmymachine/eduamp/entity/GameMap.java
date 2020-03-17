package com.itworksonmymachine.eduamp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "game_map")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameMap extends Auditable<String> {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Getter
  @Setter
  private String title;

  @Getter
  @Setter
  @Column(columnDefinition = "TEXT")
  private String description;

  @Getter
  @Setter
  private String mapDescriptor;

  @Getter
  @Setter
  @OneToMany(mappedBy = "gameMap", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JsonManagedReference
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<Question> questions;

  @Getter
  @Setter
  @OneToMany(mappedBy = "gameMap", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JsonManagedReference
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<LearningMaterial> learningMaterials;

  @Getter
  @Setter
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "topic_id")
  // https://stackoverflow.com/questions/49592081/jpa-detached-entity-passed-to-persist-nested-exception-is-org-hibernate-persis
  private Topic topic;

  @Getter
  @Setter
  private boolean isPlayable;

}

