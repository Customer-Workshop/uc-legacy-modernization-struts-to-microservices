package com.northstar.settlement.service;

import com.northstar.settlement.model.Settlement;
import org.springframework.stereotype.Component;

/** Calculates settlement values using the arithmetic rules used by adjusters. */
@Component
public class SettlementCalculator {

  /** Applies depreciation, deductible, policy cap, and cent rounding. */
  public Settlement calculate(
      double coveredAmount, String deductible, double depreciation, double policyLimit) {
    double deductibleValue = 0;
    if (deductible != null && deductible.trim().length() > 0) {
      deductibleValue = Double.parseDouble(deductible);
    }
    double gross = coveredAmount - depreciation;
    double afterDeductible = gross - deductibleValue;
    if (afterDeductible < 0) {
      afterDeductible = 0;
    }
    boolean capped = afterDeductible > policyLimit;
    double amount = capped ? policyLimit : afterDeductible;
    // legacy-faithful: double arithmetic with Math.round, not BigDecimal HALF_UP,
    // so a covered amount of 1.005 settles at 1.00 exactly as the Struts app did.
    double rounded = Math.round(amount * 100.0) / 100.0;
    Settlement result = new Settlement();
    result.setCoveredAmount(coveredAmount);
    result.setDeductibleApplied(deductibleValue);
    result.setDepreciation(depreciation);
    result.setCappedAtLimit(capped);
    result.setSettlementAmount(rounded);
    return result;
  }

  public double round(double amount) {
    return Math.round(amount * 100.0) / 100.0;
  }
}
