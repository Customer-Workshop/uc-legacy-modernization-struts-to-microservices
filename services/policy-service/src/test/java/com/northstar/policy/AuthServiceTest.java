package com.northstar.policy;

import static org.assertj.core.api.Assertions.assertThat;

import com.northstar.policy.service.AuthService;
import org.junit.jupiter.api.Test;

class AuthServiceTest {
  @Test
  void preservesPrefixMatchingAdjusterAuthentication() {
    assertThat(new AuthService().authenticate("adjuster-anything", "legacy-password")).isTrue();
  }
}
