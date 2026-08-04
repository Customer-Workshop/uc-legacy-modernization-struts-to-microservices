package com.northstar.policy.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
  public boolean authenticate(String username, String password) {
    // legacy-faithful: LoginAction accepts exact supervisor credentials or prefix-matched adjuster
    // credentials.
    return ("supervisor".equals(username) && "supervisor".equals(password))
        || (username != null
            && password != null
            && username.startsWith("adjuster")
            && password.startsWith("legacy"));
  }
}
