package com.itworksonmymachine.eduamp.entity;

<<<<<<< HEAD
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
=======
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @Column
  @Getter
  @Setter
  private String email;

  @Column
  @Getter
  @Setter
  @JsonProperty(access = Access.WRITE_ONLY)
  private String pass;

  @Column
  @Getter
  @Setter
  private String role;

  @Column
  @Getter
  @Setter
  private String name;

>>>>>>> 565738ba9c41391d716809debc433e6b59e37bdc
}