package com.itworksonmymachine.eduamp.entity.unimpl;

import com.itworksonmymachine.eduamp.entity.Question;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class GameMap {

  @Getter
  @Setter
  public String mapDescriptor;

  @Getter
  @Setter
  public int mapID;

  @Getter
  @Setter
  public Map<Coordinate, Question> map;

}