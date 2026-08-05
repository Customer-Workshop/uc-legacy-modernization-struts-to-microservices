package com.northstar.policy;

import static org.assertj.core.api.Assertions.assertThat;

import com.northstar.policy.service.AuthService;
import org.junit.jupiter.api.Test;

class AuthServiceTest {
  @Test
  void preservesPrefixMatchingAdjusterAuthentication() {
    assertThat(new AuthService().authenticate("adjuster-anything", "legacy-password")).isTrue();
  }

  @Test
  void loginRequiresExactSupervisorCredentialsOrAdjusterPrefixes() {
    AuthService service = new AuthService();
    assertThat(service.authenticate("supervisor", "supervisor")).isTrue();
    assertThat(service.authenticate("supervisor", "legacy-password")).isFalse();
    assertThat(service.authenticate("adjuster9", "legacy9")).isTrue();
    assertThat(service.authenticate("clerk", "legacy-password")).isFalse();
    assertThat(service.authenticate("adjuster1", "wrong")).isFalse();
  }
}
