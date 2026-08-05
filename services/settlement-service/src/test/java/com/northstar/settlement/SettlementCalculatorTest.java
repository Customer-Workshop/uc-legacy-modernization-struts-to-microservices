package com.northstar.settlement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.northstar.settlement.service.SettlementCalculator;
import org.junit.jupiter.api.Test;

/** Locks in the legacy settlement arithmetic migrated from the Struts monolith. */
class SettlementCalculatorTest {

  private final SettlementCalculator calculator = new SettlementCalculator();

  @Test
  void normalCase() {
    var result = calculator.calculate(10000, "500", 100, 20000);
    assertEquals(9400.0, result.settlementAmount());
    assertEquals(500.0, result.deductibleApplied());
    assertFalse(result.cappedAtLimit());
  }

  @Test
  void blankDeductibleIsTreatedAsZero() {
    var result = calculator.calculate(10000, "", 100, 20000);
    assertEquals(9900.0, result.settlementAmount());
    assertEquals(0.0, result.deductibleApplied());
  }

  @Test
  void deductibleExceedingLossFloorsAtZero() {
    var result = calculator.calculate(1000, "2000", 100, 20000);
    assertEquals(0.0, result.settlementAmount());
  }

  @Test
  void policyLimitCaps() {
    var result = calculator.calculate(20000, "500", 100, 10000);
    assertEquals(10000.0, result.settlementAmount());
    assertTrue(result.cappedAtLimit());
  }

  @Test
  void halfCentUsesLegacyDoubleMath() {
    // legacy quirk: 1.005 in binary double sits just below 1.005, so Math.round pays 1.00
    // where BigDecimal half-up arithmetic would pay 1.01.
    var result = calculator.calculate(1.005, "0", 0, 100);
    assertEquals(1.0, result.settlementAmount());
  }

  @Test
  void zeroLimitDoesNotProduceNegativeAmount() {
    var result = calculator.calculate(100, "0", 0, 0);
    assertEquals(0.0, result.settlementAmount());
  }
}
