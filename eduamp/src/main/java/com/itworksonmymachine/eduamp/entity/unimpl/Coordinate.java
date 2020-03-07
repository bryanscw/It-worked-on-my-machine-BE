package com.itworksonmymachine.eduamp.entity.unimpl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Coordinate {

  @Getter
  @Setter
  public int x;

  @Getter
  @Setter
  public int y;

}
