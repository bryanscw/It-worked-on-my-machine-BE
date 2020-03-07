package com.itworksonmymachine.eduamp.entity;

import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {

  @Getter
  @Setter
  private int x;

  @Getter
  @Setter
  private int y;

}
