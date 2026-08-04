package com.northstar.settlement.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SettlementCalculatorTest {
  private final SettlementCalculator calculator = new SettlementCalculator();

  @Test
  void blankDeductibleIsZero() {
    assertThat(calculator.calculate(119, 5000, "", 0, 1000).deductibleApplied()).isZero();
  }

  @Test
  void javaDoubleHalfCentRoundsDown() {
    // legacy-faithful: Math.round(1.005 * 100.0) / 100.0 produces 1.00.
    assertThat(calculator.calculate(119, 1.005, "0", 0, 1000).settlementAmount()).isEqualTo(1.0);
  }

  @Test
  void capIsStrictAndRoundedAfterCap() {
    assertThat(calculator.calculate(119, 1000.001, "0", 0, 1000).cappedAtLimit()).isTrue();
    assertThat(calculator.calculate(119, 1000, "0", 0, 1000).cappedAtLimit()).isFalse();
  }

  @Test
  void deductibleFloorPrecedesCap() {
    assertThat(calculator.calculate(119, 10, "20", 0, 5).settlementAmount()).isZero();
  }

  @Test
  void depreciationIsSubtractedBeforeDeductible() {
    assertThat(calculator.calculate(119, 100, "10", 20, 1000).settlementAmount()).isEqualTo(70.0);
  }
}
