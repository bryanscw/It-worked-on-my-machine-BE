package com.itworksonmymachine.eduamp.Entity;

import lombok.Getter;
import lombok.Setter;

public class LearningMaterial{

    @Getter @Setter
    public int learningMaterialID;

    @Getter @Setter
    public String title;

    @Getter @Setter
    public String link;

    @Getter @Setter
    public String description;
}