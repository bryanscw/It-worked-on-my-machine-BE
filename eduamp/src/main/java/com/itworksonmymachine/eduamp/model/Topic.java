package com.itworksonmymachine.eduamp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;

public class Topic{

    @Getter @Setter
    public String title;

    @Getter @Setter
    public String description;

    @Getter @Setter
    public int topicID;

    @Getter @Setter
    public ArrayList<Level> level;

    @Getter @Setter
    public Date createdAt;
}