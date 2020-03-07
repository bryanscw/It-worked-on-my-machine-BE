package com.itworksonmymachine.eduamp.entity;

import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LearningMaterial{

    @Id
    @Getter @Setter
    public int learningMaterialID;

    @Getter @Setter
    public String title;

    @Getter @Setter
    public String link;

    @Getter @Setter
    public String description;

}