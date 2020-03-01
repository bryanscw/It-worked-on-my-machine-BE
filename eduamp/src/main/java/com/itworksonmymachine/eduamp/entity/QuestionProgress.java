package com.itworksonmymachine.eduamp.entity;

import lombok.Getter;
import lombok.Setter;

public class QuestionProgress{

    @Getter @Setter
    public int questionID;

    @Getter @Setter
    public int attemptCount;

    @Getter @Setter
    public boolean isCleared;
}