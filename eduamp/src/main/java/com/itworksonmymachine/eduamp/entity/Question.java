package com.itworksonmymachine.eduamp.entity;

import com.itworksonmymachine.eduamp.model.Coordinates;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
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
  private GameMap gameMap;

  @Getter
  @Setter
  private String questionText;

  @Getter
  @Setter
  private int answer = -1;

  @Getter
  @Setter
  @ElementCollection
  private Map<Integer, String> options;

  @Getter
  @Setter
  private Coordinates coordinates;

}