package com.itworksonmymachine.eduamp.entity;

import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "game_map")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameMap {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Getter
  @Setter
  private String mapDescriptor;

  @Getter
  @Setter
  @ElementCollection
  private Map<Coordinates, Question> map;

  @OneToOne
  @PrimaryKeyJoinColumn
  @Getter
  @Setter
  private Level level;

}
