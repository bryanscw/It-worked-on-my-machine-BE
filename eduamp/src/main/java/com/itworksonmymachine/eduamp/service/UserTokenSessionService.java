package com.itworksonmymachine.eduamp.service;

import com.itworksonmymachine.eduamp.model.UserTokenSession;
import java.security.Principal;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserTokenSessionService {

  /**
   * Check whether there is mapping between oauth token, username and session-id. And the token is
   * not yet expired.
   *
   * @param userTokenSession
   * @return ValidMappingResponse if valid mapping else throw {@link UsernameNotFoundException}
   */
  ValidMappingResponse isValidUserTokenSessionMapping(UserTokenSession userTokenSession)
      throws UsernameNotFoundException;

  /**
   * @param userTokenSession
   * @return token session record from data base.
   */
  UserTokenSession saveUserTokenSessionMapping(UserTokenSession userTokenSession);

  /**
   * @param principal,httpHeaders
   * @return token session record from data base.
   */
  UserTokenSession buildUserTokenSession(Principal principal, HttpHeaders httpHeaders);


  /**
   * Class to store isValidUserTokenSessionMapping() response.
   */
  class ValidMappingResponse {

    private boolean valid;
    private UserTokenSession userTokenSession;

    public ValidMappingResponse() {
    }

    public ValidMappingResponse(boolean valid, UserTokenSession userTokenSession) {
      this.valid = valid;
      this.userTokenSession = userTokenSession;
    }

    public boolean isValid() {
      return valid;
    }

    public void setValid(boolean valid) {
      this.valid = valid;
    }

    public UserTokenSession getUserTokenSession() {
      return userTokenSession;
    }

    public void setUserTokenSession(UserTokenSession userTokenSession) {
      this.userTokenSession = userTokenSession;
    }
  }

}
