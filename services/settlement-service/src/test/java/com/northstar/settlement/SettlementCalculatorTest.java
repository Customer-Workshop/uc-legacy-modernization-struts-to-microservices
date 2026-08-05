package com.northstar.settlement;

import static org.junit.jupiter.api.Assertions.*;

import com.northstar.settlement.dto.SettlementResponse;
import com.northstar.settlement.service.SettlementCalculator;
import org.junit.jupiter.api.Test;

/** Locks in the legacy settlement arithmetic, including each reproduced quirk. */
class SettlementCalculatorTest {

  @Test
  void normalCase() {
    var result = SettlementCalculator.calculate(10000, "500", 100, 20000);
    assertEquals(9400.0, result.settlementAmount());
    assertEquals(500.0, result.deductibleApplied());
    assertFalse(result.cappedAtLimit());
  }

  @Test
  void blankDeductibleCoercesToZero() {
    var result = SettlementCalculator.calculate(10000, "", 100, 20000);
    assertEquals(9900.0, result.settlementAmount());
    assertEquals(0.0, result.deductibleApplied());
  }

  @Test
  void nullDeductibleCoercesToZero() {
    var result = SettlementCalculator.calculate(10000, null, 100, 20000);
    assertEquals(9900.0, result.settlementAmount());
  }

  @Test
  void deductibleExceedingLossFloorsAtZero() {
    var result = SettlementCalculator.calculate(1000, "2000", 0, 20000);
    assertEquals(0.0, result.settlementAmount());
    assertFalse(result.cappedAtLimit());
  }

  @Test
  void policyLimitCaps() {
    var result = SettlementCalculator.calculate(20000, "100", 0, 1000);
    assertEquals(1000.0, result.settlementAmount());
    assertTrue(result.cappedAtLimit());
  }

  @Test
  void halfCentUsesLegacyDoubleMath() {
    // legacy quirk: 1.005 in binary double sits just below the half cent, so the
    // legacy Math.round arithmetic pays 1.00 where BigDecimal HALF_UP would pay 1.01.
    var result = SettlementCalculator.calculate(1.005, "0", 0, 100);
    assertEquals(1.0, result.settlementAmount());
  }

  @Test
  void zeroLimitDoesNotProduceNegativeAmount() {
    var result = SettlementCalculator.calculate(100, "0", 0, 0);
    assertEquals(0.0, result.settlementAmount());
  }

  @Test
  void moneyRenderingMatchesLegacyFieldTag() {
    var response = SettlementResponse.from(SettlementCalculator.calculate(1.005, "", 0, 100));
    assertEquals("1.01", response.coveredAmount());
    assertEquals("1.00", response.settlementAmount());
    assertEquals("0.00", response.deductibleApplied());
    assertEquals("false", response.cappedAtLimit());
  }
}
