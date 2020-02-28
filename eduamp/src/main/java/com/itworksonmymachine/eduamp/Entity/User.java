package com.itworksonmymachine.eduamp.Entity;

import lombok.Getter;
import lombok.Setter;

public class User{

    @Getter @Setter
    private int userID;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password;

    enum Role {
        Student, Teacher, Admin
    }
}