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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "topic")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Topic extends Auditable<String> {

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
  @OneToMany(mappedBy = "topic", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE,
      CascadeType.REFRESH, CascadeType.REMOVE})
  @JsonManagedReference
  private Set<GameMap> gameMap;

}
