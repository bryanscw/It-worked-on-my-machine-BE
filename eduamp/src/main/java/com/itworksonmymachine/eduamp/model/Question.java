package com.itworksonmymachine.eduamp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class Question{

    @Getter @Setter
    public int questionID;

    @Getter @Setter
    public String questionText;

    @Getter @Setter
    public int answer;

    @Getter @Setter
    public Map<Integer, String> options;
}