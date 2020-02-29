package com.itworksonmymachine.eduamp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

  @Id
  @Column
  @Getter @Setter
  private String userEmail;

  @Column
  @Getter @Setter
  private String userPass;

  @Column
  @Getter @Setter
  private String userRole;

}