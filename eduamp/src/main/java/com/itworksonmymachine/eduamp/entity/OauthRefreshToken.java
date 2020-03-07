package com.itworksonmymachine.eduamp.entity;

import java.sql.Blob;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "oauth_refresh_token")
@AllArgsConstructor
@NoArgsConstructor
public class OauthRefreshToken {

  @Id
  @Column
  @Getter
  @Setter
  private String tokenId;

  @Column
  @Lob
  @Getter
  @Setter
  private Blob token;

  @Column
  @Lob
  @Getter
  @Setter
  private Blob authentication;

}
