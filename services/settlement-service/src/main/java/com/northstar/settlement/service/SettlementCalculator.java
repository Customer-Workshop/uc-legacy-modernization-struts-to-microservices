package com.northstar.settlement.service;

import com.northstar.settlement.model.Settlement;
import org.springframework.stereotype.Service;

@Service
public class SettlementCalculator {
  public Settlement calculate(
      int claimId,
      double coveredAmount,
      String deductible,
      double depreciation,
      double policyLimit) {
    // legacy-faithful: blank deductible is coerced to zero before double arithmetic.
    double deductibleValue =
        deductible != null && deductible.trim().length() > 0 ? Double.parseDouble(deductible) : 0;
    double gross = coveredAmount - depreciation;
    double afterDeductible = gross - deductibleValue;
    // legacy-faithful: negative post-deductible settlements are floored at zero.
    if (afterDeductible < 0) afterDeductible = 0;
    boolean capped = afterDeductible > policyLimit;
    double amount = capped ? policyLimit : afterDeductible;
    // legacy-faithful: settlement arithmetic rounds the binary double with Math.round.
    double rounded = Math.round(amount * 100.0) / 100.0;
    return new Settlement(
        null, claimId, coveredAmount, deductibleValue, depreciation, rounded, capped, null, null);
  }
}
