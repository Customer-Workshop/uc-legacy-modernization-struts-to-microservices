package com.northstar.settlement;

import static org.assertj.core.api.Assertions.assertThat;

import com.northstar.settlement.service.LegacyCoercions;
import org.junit.jupiter.api.Test;

/** Locks in the Struts request-parameter fallbacks the screens depended on. */
class LegacyCoercionsTest {
  @Test
  void unparseableClaimIdFallsBackToScreenDefault() {
    assertThat(LegacyCoercions.integer(null, 119)).isEqualTo(119);
    assertThat(LegacyCoercions.integer("", 119)).isEqualTo(119);
    assertThat(LegacyCoercions.integer("abc", 119)).isEqualTo(119);
    assertThat(LegacyCoercions.integer("120", 119)).isEqualTo(120);
  }

  @Test
  void unparseableAmountFallsBackToScreenDefault() {
    assertThat(LegacyCoercions.decimal(null, 5000)).isEqualTo(5000.0);
    assertThat(LegacyCoercions.decimal("", 5000)).isEqualTo(5000.0);
    assertThat(LegacyCoercions.decimal("12.5", 5000)).isEqualTo(12.5);
  }
}
