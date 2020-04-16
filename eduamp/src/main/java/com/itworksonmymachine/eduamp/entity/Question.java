package com.itworksonmymachine.eduamp.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.itworksonmymachine.eduamp.model.Coordinates;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question")
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Question extends Auditable<String> {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Getter
  @Setter
  @ManyToOne(cascade = {CascadeType.REFRESH})
  @JoinColumn(name = "game_map_id", nullable = false)
  @JsonIdentityReference(alwaysAsId = true)
  private GameMap gameMap;

  @Getter
  @Setter
  private String questionText;

  @Getter
  @Setter
  private int answer = -1;

  @Getter
  @Setter
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "question_option_mapping",
      joinColumns = {@JoinColumn(name = "question_id", referencedColumnName = "id")})
  @MapKeyColumn(name = "option_index")
  @Column(name = "answer")
  private Map<Integer, String> options;

  @Getter
  @Setter
  private Coordinates coordinates;

}
