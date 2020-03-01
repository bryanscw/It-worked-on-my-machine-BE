package com.itworksonmymachine.eduamp.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class GameMap{

    @Getter @Setter
    public String mapDescriptor;

    @Getter @Setter
    public int mapID;

    @Getter @Setter
    public Map<Coordinate, Question> map;
}