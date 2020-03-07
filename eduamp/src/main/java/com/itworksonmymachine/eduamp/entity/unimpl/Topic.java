package com.itworksonmymachine.eduamp.entity.unimpl;

import com.itworksonmymachine.eduamp.entity.Level;
import java.util.ArrayList;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Topic {

  @Getter
  @Setter
  public String title;

  @Getter
  @Setter
  public String description;

  @Getter
  @Setter
  public int topicID;

  @Getter
  @Setter
  public ArrayList<Level> level;

  @Getter
  @Setter
  public Date createdAt;

}