package com.northstar.settlement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.northstar.settlement.service.LegacySettlementCalculator;
import org.junit.jupiter.api.Test;

class LegacySettlementCalculatorTest {

  @Test
  void halfCentRoundsDownLikeLegacyDoubleMath() {
    // 1.005 is a hair below 1.005 in binary floating point, so the legacy app pays 1.00.
    var result = LegacySettlementCalculator.calculate(1.005, "", 0, 1000);
    assertThat(result.settlementAmount()).isEqualTo(1.00);
    assertThat(result.cappedAtLimit()).isFalse();
  }

  @Test
  void blankDeductibleIsCoercedToZero() {
    var result = LegacySettlementCalculator.calculate(5000, "", 500, 100000);
    assertThat(result.deductibleApplied()).isEqualTo(0.0);
    assertThat(result.settlementAmount()).isEqualTo(4500.00);
  }

  @Test
  void settlementIsCappedAtThePolicyLimit() {
    var result = LegacySettlementCalculator.calculate(20000, "100.00", 0, 1000);
    assertThat(result.cappedAtLimit()).isTrue();
    assertThat(result.settlementAmount()).isEqualTo(1000.00);
  }

  @Test
  void deductibleLargerThanLossFloorsAtZero() {
    var result = LegacySettlementCalculator.calculate(1000, "2000.00", 0, 100000);
    assertThat(result.cappedAtLimit()).isFalse();
    assertThat(result.settlementAmount()).isEqualTo(0.00);
  }

  @Test
  void depreciationIsSubtractedBeforeTheDeductible() {
    var result = LegacySettlementCalculator.calculate(5000, "500.00", 1000, 100000);
    assertThat(result.settlementAmount()).isEqualTo(3500.00);
    assertThat(result.cappedAtLimit()).isFalse();
  }

  @Test
  void unparseableDeductibleThrowsLikeLegacyParseDouble() {
    assertThatThrownBy(() -> LegacySettlementCalculator.calculate(5000, "abc", 0, 1000))
        .isInstanceOf(NumberFormatException.class);
  }
}
