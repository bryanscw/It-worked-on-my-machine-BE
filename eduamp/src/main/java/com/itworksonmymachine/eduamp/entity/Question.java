package com.itworksonmymachine.eduamp.entity;

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
import org.codehaus.jackson.annotate.JsonBackReference;

@Entity
@Table(name="question")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Question extends Auditable<String>  {

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "level_id", nullable = false)
  private Level level;

  @Getter
  @Setter
  private String questionText;

  @Getter
  @Setter
  private int answer;

  @Getter
  @Setter
  @ElementCollection
  private Map<Integer, String> options;

}