package com.northstar.settlement.service;

import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.exception.NotFoundException;
import com.northstar.settlement.model.Claim;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Policy;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.ClaimRepository;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.PolicyRepository;
import com.northstar.settlement.repository.SettlementRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementApplicationService {
  // legacy-faithful: SettlementCalculateAction/PaymentIssueAction default claimId to 119.
  private static final int DEFAULT_CLAIM_ID = 119;
  // legacy-faithful: calculate falls back to a 10000 limit when the claim or policy is missing.
  private static final double DEFAULT_POLICY_LIMIT = 10000;
  // legacy-faithful: covered amount defaults to 5000 when blank or unparseable.
  private static final double DEFAULT_COVERED_AMOUNT = 5000;
  // legacy-faithful: the Struts session operator; the settlement screens are supervisor-only.
  private static final String OPERATOR = "supervisor";

  private final SettlementRepository settlements;
  private final PaymentRepository payments;
  private final ClaimRepository claims;
  private final PolicyRepository policies;
  private final DataSource dataSource;

  public SettlementApplicationService(
      SettlementRepository settlements,
      PaymentRepository payments,
      ClaimRepository claims,
      PolicyRepository policies,
      DataSource dataSource) {
    this.settlements = settlements;
    this.payments = payments;
    this.claims = claims;
    this.policies = policies;
    this.dataSource = dataSource;
  }

  public SettlementCalculator.Outcome calculate(SettlementRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    double limit = policyLimit(claimId);
    return SettlementCalculator.calculate(
        decimal(request.coveredAmount(), DEFAULT_COVERED_AMOUNT),
        blankToZero(request.deductible()),
        decimal(request.depreciation(), 0),
        limit);
  }

  @Transactional
  public Settlement save(SettlementRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    Claim claim =
        claims.findById(claimId).orElseThrow(() -> new NotFoundException("claim.notFound"));
    Policy policy =
        policies
            .findById(claim.getPolicyId())
            .orElseThrow(() -> new NotFoundException("policy.notFound"));
    SettlementCalculator.Outcome outcome =
        SettlementCalculator.calculate(
            decimal(request.coveredAmount(), DEFAULT_COVERED_AMOUNT),
            blankToZero(request.deductible()),
            decimal(request.depreciation(), 0),
            policy.getPolicyLimit().doubleValue());
    Settlement settlement =
        new Settlement(
            settlements.nextId(),
            claimId,
            BigDecimal.valueOf(outcome.coveredAmount()),
            BigDecimal.valueOf(outcome.deductibleApplied()),
            BigDecimal.valueOf(outcome.depreciation()),
            outcome.cappedAtLimit(),
            BigDecimal.valueOf(outcome.settlementAmount()),
            OPERATOR,
            // legacy-faithful: SettlementSaveAction stamps the fixed operator date.
            LocalDate.of(2019, 4, 1));
    return settlements.save(settlement);
  }

  @Transactional
  public Payment issuePayment(PaymentRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    Settlement settlement =
        settlements
            .findTopByClaimIdOrderBySettlementIdDesc(claimId)
            .orElseThrow(() -> new NotFoundException("settlement.notFound"));
    int paymentId = payments.nextId();
    Payment payment =
        new Payment(
            paymentId,
            claimId,
            settlement.getSettlementId(),
            request.payeeName(),
            BigDecimal.valueOf(
                decimal(request.amount(), settlement.getSettlementAmount().doubleValue())),
            request.paymentMethod(),
            "CHK-" + paymentId,
            // legacy-faithful: PaymentIssueAction stamps the fixed issue date.
            LocalDate.of(2019, 4, 3),
            "ISSUED");
    return payments.save(payment);
  }

  public List<Payment> history(String claimId) {
    return payments.findByClaimIdOrderByPaymentId(integer(claimId, DEFAULT_CLAIM_ID));
  }

  public Payment getPayment(int id) {
    return payments.findById(id).orElseThrow(() -> new NotFoundException("payment.notFound"));
  }

  public Settlement latestByClaim(int claimId) {
    return settlements
        .findTopByClaimIdOrderBySettlementIdDesc(claimId)
        .orElseThrow(() -> new NotFoundException("settlement.notFound"));
  }

  public List<Settlement> all() {
    return settlements.findAll(Sort.by("settlementId"));
  }

  public List<Payment> allPayments() {
    return payments.findAll();
  }

  @Transactional
  public void reset() {
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-settlement.sql"))
        .execute(dataSource);
  }

  private double policyLimit(int claimId) {
    return claims
        .findById(claimId)
        .flatMap(claim -> policies.findById(claim.getPolicyId()))
        .map(policy -> policy.getPolicyLimit().doubleValue())
        .orElse(DEFAULT_POLICY_LIMIT);
  }

  // legacy-faithful: ClaimsActionSupport.integer swallows conversion failures.
  private int integer(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }

  // legacy-faithful: ClaimsActionSupport.decimal swallows conversion failures.
  private double decimal(String value, double fallback) {
    try {
      return Double.parseDouble(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }

  // legacy-faithful: the actions pass "0" for null/empty deductible before calculating.
  private String blankToZero(String value) {
    return value == null || value.isEmpty() ? "0" : value;
  }
}
