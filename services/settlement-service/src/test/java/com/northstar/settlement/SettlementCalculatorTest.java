package com.northstar.settlement;

import static org.junit.jupiter.api.Assertions.*;

import com.northstar.settlement.service.SettlementCalculator;
import org.junit.jupiter.api.Test;

class SettlementCalculatorTest {
  private final SettlementCalculator calculator = new SettlementCalculator();

  @Test
  void standardCalculationCapsAtPolicyLimit() {
    var value = calculator.calculate(5000, "500.00", 0, 1000);
    assertEquals(1000.00, value.settlementAmount());
    assertTrue(value.cappedAtLimit());
    assertEquals(500.00, value.deductibleApplied());
  }

  @Test
  void halfCentRoundsDownLikeLegacyDoubleMath() {
    // legacy quirk: 1.005 in double is a hair below the half cent, so legacy pays 1.00.
    var value = calculator.calculate(1.005, "0", 0, 100000);
    assertEquals(1.00, value.settlementAmount());
    assertFalse(value.cappedAtLimit());
  }

  @Test
  void blankDeductibleIsTreatedAsZero() {
    var value = calculator.calculate(5000, "", 500, 100000);
    assertEquals(4500.00, value.settlementAmount());
    assertEquals(0.00, value.deductibleApplied());
    assertFalse(value.cappedAtLimit());
  }

  @Test
  void deductibleAboveLossFloorsSettlementAtZero() {
    var value = calculator.calculate(1000, "2000", 0, 100000);
    assertEquals(0.00, value.settlementAmount());
    assertEquals(2000.00, value.deductibleApplied());
    assertFalse(value.cappedAtLimit());
  }

  @Test
  void policyCapAppliesAfterDeductible() {
    var value = calculator.calculate(20000, "100.00", 0, 1000);
    assertEquals(1000.00, value.settlementAmount());
    assertTrue(value.cappedAtLimit());
  }

  @Test
  void cappedFlagIsFalseWhenExactlyAtLimit() {
    var value = calculator.calculate(1000, "0", 0, 1000);
    assertEquals(1000.00, value.settlementAmount());
    assertFalse(value.cappedAtLimit());
  }
}
