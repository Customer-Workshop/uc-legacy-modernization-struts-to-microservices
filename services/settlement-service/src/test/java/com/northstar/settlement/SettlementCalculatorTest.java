package com.northstar.settlement;

import static org.assertj.core.api.Assertions.assertThat;

import com.northstar.settlement.service.LegacyCoercions;
import com.northstar.settlement.service.LegacyMoney;
import com.northstar.settlement.service.SettlementCalculator;
import org.junit.jupiter.api.Test;

class SettlementCalculatorTest {
  @Test
  void capsAtPolicyLimit() {
    var result = SettlementCalculator.calculate(20000, "100", 0, 1000);
    assertThat(result.cappedAtLimit()).isTrue();
    assertThat(result.settlementAmount()).isEqualTo(1000.0);
  }

  @Test
  void appliesDepreciationAndDeductible() {
    var result = SettlementCalculator.calculate(5000, "", 500, 100000);
    assertThat(result.deductibleApplied()).isEqualTo(0.0);
    assertThat(result.settlementAmount()).isEqualTo(4500.0);
    assertThat(result.cappedAtLimit()).isFalse();
  }

  @Test
  void blankDeductibleCoercesToZero() {
    var blank = SettlementCalculator.calculate(5000, "   ", 0, 100000);
    var absent = SettlementCalculator.calculate(5000, null, 0, 100000);
    assertThat(blank.deductibleApplied()).isEqualTo(0.0);
    assertThat(absent.deductibleApplied()).isEqualTo(0.0);
  }

  @Test
  void deductibleAboveLossFloorsAtZero() {
    var result = SettlementCalculator.calculate(1000, "2000", 0, 100000);
    assertThat(result.settlementAmount()).isEqualTo(0.0);
    assertThat(result.cappedAtLimit()).isFalse();
  }

  @Test
  void halfCentRoundsDownLikeLegacyDoubleMath() {
    // 1.005 in binary double is just below the half cent, so the legacy pays 1.00, not 1.01.
    var result = SettlementCalculator.calculate(1.005, "", 0, 100000);
    assertThat(result.settlementAmount()).isEqualTo(1.00);
    assertThat(LegacyMoney.format(result.settlementAmount())).isEqualTo("1.00");
  }

  @Test
  void moneyFormatUsesShortestDecimalRepresentation() {
    // The legacy JSP displayed the raw covered amount as 1.01 via String.format("%.2f", 1.005).
    assertThat(LegacyMoney.format(1.005)).isEqualTo("1.01");
  }

  @Test
  void requestCoercionsFallBackLikeStruts() {
    assertThat(LegacyCoercions.integer("not-a-number", 119)).isEqualTo(119);
    assertThat(LegacyCoercions.integer(null, 119)).isEqualTo(119);
    assertThat(LegacyCoercions.decimal("garbage", 5000)).isEqualTo(5000.0);
    assertThat(LegacyCoercions.decimal("1000.00", 0)).isEqualTo(1000.0);
  }
}
