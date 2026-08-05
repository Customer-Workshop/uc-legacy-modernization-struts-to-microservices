package com.northstar.settlement;

import static org.assertj.core.api.Assertions.assertThat;

import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.service.SettlementCalculator;
import org.junit.jupiter.api.Test;

/** Locks in the legacy settlement arithmetic, including each reproduced quirk. */
class SettlementCalculatorTest {
  private final SettlementCalculator calculator = new SettlementCalculator();

  @Test
  void normalCase() {
    Settlement result = calculator.calculate(10000, "500", 100, 20000);
    assertThat(result.getSettlementAmount()).isEqualTo(9400.0);
    assertThat(result.getDeductibleApplied()).isEqualTo(500.0);
  }

  @Test
  void blankDeductibleAppliesZero() {
    Settlement result = calculator.calculate(10000, "", 100, 20000);
    assertThat(result.getSettlementAmount()).isEqualTo(9900.0);
    assertThat(result.getDeductibleApplied()).isEqualTo(0.0);
  }

  @Test
  void deductibleExceedingLossFloorsAtZero() {
    Settlement result = calculator.calculate(1000, "2000", 0, 20000);
    assertThat(result.getSettlementAmount()).isEqualTo(0.0);
    assertThat(result.isCappedAtLimit()).isFalse();
  }

  @Test
  void policyLimitCaps() {
    Settlement result = calculator.calculate(20000, "100", 0, 1000);
    assertThat(result.getSettlementAmount()).isEqualTo(1000.0);
    assertThat(result.isCappedAtLimit()).isTrue();
  }

  @Test
  void halfCentUsesLegacyDoubleMath() {
    // legacy quirk: 1.005 in binary double sits below 1.005, so the legacy app pays 1.00,
    // where BigDecimal HALF_UP arithmetic would pay 1.01.
    Settlement result = calculator.calculate(1.005, "", 0, 100000);
    assertThat(result.getSettlementAmount()).isEqualTo(1.0);
  }

  @Test
  void zeroLimitDoesNotProduceNegativeAmount() {
    Settlement result = calculator.calculate(100, "0", 0, 0);
    assertThat(result.getSettlementAmount()).isEqualTo(0.0);
  }
}
