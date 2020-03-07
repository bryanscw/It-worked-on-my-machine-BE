package com.itworksonmymachine.eduamp.entity;

import com.itworksonmymachine.eduamp.entity.unimpl.GameMap;
import java.util.ArrayList;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Level {

  @Id
  @Getter
  @Setter
  public int levelID;

  @Getter
  @Setter
  public ArrayList<LearningMaterial> learningMaterials;

  @Getter
  @Setter
  public ArrayList<Question> questions;

  @Getter
  @Setter
  public GameMap map;

  @Getter
  @Setter
  public boolean isPlayable;

}