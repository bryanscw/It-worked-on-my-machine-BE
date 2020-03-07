package com.itworksonmymachine.eduamp.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
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
public class Topic {

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
  private String description;

  @Getter
  @Setter
  @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
  private List<Level> levels;

  @Getter
  @Setter
  private Date createdAt;

}