package com.northstar.settlement;

import static org.junit.jupiter.api.Assertions.*;

import com.northstar.settlement.dto.SettlementResponse;
import com.northstar.settlement.service.SettlementCalculator;
import org.junit.jupiter.api.Test;

class SettlementCalculatorTest {
  private final SettlementCalculator calculator = new SettlementCalculator();

  @Test
  void halfCentUsesLegacyDoubleRounding() {
    var result = calculator.calculate(120, 1.005, "", 0, 10000);
    assertEquals(1.00, result.getSettlementAmount());
    assertEquals(1.005, result.getCoveredAmount());
  }

  @Test
  void policyCapUsesLimit() {
    var result = calculator.calculate(119, 20000, "100", 0, 1000);
    assertTrue(result.isCappedAtLimit());
    assertEquals(1000, result.getSettlementAmount());
  }

  @Test
  void deductibleFloorStopsAtZero() {
    assertEquals(0, calculator.calculate(120, 1000, "2000", 0, 10000).getSettlementAmount());
  }

  @Test
  void blankDeductibleIsZero() {
    assertEquals(0, calculator.calculate(120, 500, "", 0, 10000).getDeductibleApplied());
    assertEquals(500, calculator.calculate(120, 500, "", 0, 10000).getSettlementAmount());
    assertEquals(0, calculator.calculate(120, 500, "   ", 0, 10000).getDeductibleApplied());
  }

  @Test
  void fieldTagFormattingPreservesCoveredAndRoundedSettlementValues() {
    var result = calculator.calculate(120, 1.005, "", 0, 10000);
    var response = SettlementResponse.calculated(result);

    assertEquals("1.01", response.coveredAmount());
    assertEquals("1.00", response.settlementAmount());
  }
}
