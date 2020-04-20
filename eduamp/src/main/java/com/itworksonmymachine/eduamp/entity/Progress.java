package com.itworksonmymachine.eduamp.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.itworksonmymachine.eduamp.model.Coordinates;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "progress")
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
  @ManyToOne(cascade = {CascadeType.REFRESH})
  @JoinColumn(name = "game_map_id", nullable = false)
  @JsonIdentityReference(alwaysAsId = true)
  private GameMap gameMap;

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
  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "progress",
      fetch = FetchType.EAGER,
      cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  @JsonIdentityReference(alwaysAsId = true)
  private Set<QuestionProgress> questionProgressSet;

}

