package com.itworksonmymachine.eduamp.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Level{

    @Getter @Setter
    public int levelID;

    @Getter @Setter
    public ArrayList<LearningMaterial> learningMaterial;

    @Getter @Setter
    public ArrayList<Question> questions;

    @Getter @Setter
    public GameMap map;

    @Getter @Setter
    public boolean isPlayable;
}