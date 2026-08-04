package com.northstar.policy.dto;

import com.northstar.policy.model.Policy;
import java.math.RoundingMode;

public record PolicyResponse(
    Integer policyId,
    String policyNumber,
    String lineOfBusiness,
    String insuredName,
    String insuredAddress,
    String effectiveDate,
    String expiryDate,
    String policyLimit,
    String deductible,
    String annualPremium,
    String status) {
  public static PolicyResponse from(Policy p) {
    return new PolicyResponse(
        p.getPolicyId(),
        p.getPolicyNumber(),
        p.getLineOfBusiness(),
        p.getInsuredName(),
        p.getInsuredAddress(),
        p.getEffectiveDate().toString(),
        p.getExpiryDate().toString(),
        money(p.getPolicyLimit()),
        money(p.getDeductible()),
        money(p.getAnnualPremium()),
        p.getStatus());
  }

  private static String money(java.math.BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }
}
