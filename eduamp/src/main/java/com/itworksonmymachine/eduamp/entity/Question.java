package com.itworksonmymachine.eduamp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import lombok.ToString;

@Entity
@Table(name = "question")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Question extends Auditable<String> {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "game_map_id", nullable = false)
  @Getter
  @Setter
  @JsonBackReference
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

  // Getter method to retrieve the gameMap_id
  public int getGameMap_id(){
    return gameMap.getId();
  }

}
