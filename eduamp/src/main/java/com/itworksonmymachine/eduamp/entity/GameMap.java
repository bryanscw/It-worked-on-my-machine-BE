package com.itworksonmymachine.eduamp.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
public class GameMap extends Auditable<String> {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Getter
  @Setter
  @OneToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "level_id")
  private Level level;

  @Getter
  @Setter
  private String mapDescriptor;

  @Getter
  @Setter
  @OneToMany(mappedBy = "gameMap", cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  private List<Question> questions;

}
