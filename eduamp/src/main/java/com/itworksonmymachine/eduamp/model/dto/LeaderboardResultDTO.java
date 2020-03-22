package com.itworksonmymachine.eduamp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LeaderboardResultDTO {

  @Getter
  @Setter
  private String name;

  @Getter
  @Setter
  private double timing;

}
