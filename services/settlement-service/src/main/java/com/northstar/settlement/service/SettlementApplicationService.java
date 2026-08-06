package com.northstar.settlement.service;

import com.northstar.settlement.dto.CalculateRequest;
import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.exception.NotFoundException;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.ClaimRepository;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.PolicyRepository;
import com.northstar.settlement.repository.SettlementRepository;
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
  // legacy-faithful defaults from ClaimsActionSupport and the settlement Actions.
  static final int DEFAULT_CLAIM_ID = 119;
  static final double DEFAULT_COVERED_AMOUNT = 5000;
  static final double DEFAULT_DEPRECIATION = 0;
  static final double DEFAULT_POLICY_LIMIT = 10000;
  static final String OPERATOR = "supervisor";
  static final LocalDate CALCULATED_DATE = LocalDate.of(2019, 4, 1);
  static final LocalDate ISSUED_DATE = LocalDate.of(2019, 4, 3);

  private final SettlementRepository settlements;
  private final PaymentRepository payments;
  private final ClaimRepository claims;
  private final PolicyRepository policies;
  private final SettlementCalculator calculator;
  private final DataSource dataSource;

  public SettlementApplicationService(
      SettlementRepository settlements,
      PaymentRepository payments,
      ClaimRepository claims,
      PolicyRepository policies,
      SettlementCalculator calculator,
      DataSource dataSource) {
    this.settlements = settlements;
    this.payments = payments;
    this.claims = claims;
    this.policies = policies;
    this.calculator = calculator;
    this.dataSource = dataSource;
  }

  public int claimId(String value) {
    return integer(value, DEFAULT_CLAIM_ID);
  }

  public SettlementCalculator.Calculation calculate(CalculateRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    double limit = policyLimit(claimId);
    double covered = decimal(request.coveredAmount(), DEFAULT_COVERED_AMOUNT);
    double depreciation = decimal(request.depreciation(), DEFAULT_DEPRECIATION);
    // legacy-faithful: a blank deductible is coerced to "0" before calculation.
    String deductible =
        request.deductible() == null || request.deductible().isEmpty() ? "0" : request.deductible();
    return calculator.calculate(covered, deductible, depreciation, limit);
  }

  @Transactional
  public Settlement save(CalculateRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    SettlementCalculator.Calculation value = calculate(request);
    Settlement settlement =
        new Settlement(
            settlements.nextId(),
            claimId,
            value.coveredAmount(),
            value.deductibleApplied(),
            value.depreciation(),
            value.cappedAtLimit(),
            value.settlementAmount(),
            OPERATOR,
            CALCULATED_DATE);
    return settlements.save(settlement);
  }

  @Transactional
  public Payment issue(PaymentRequest request) {
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
            decimal(request.amount(), settlement.getSettlementAmount()),
            request.paymentMethod(),
            "CHK-" + paymentId,
            ISSUED_DATE,
            "ISSUED");
    return payments.save(payment);
  }

  public List<Payment> history(String claimId) {
    return payments.findByClaimIdOrderByPaymentId(integer(claimId, DEFAULT_CLAIM_ID));
  }

  public Payment payment(int paymentId) {
    return payments
        .findById(paymentId)
        .orElseThrow(() -> new NotFoundException("payment.notFound"));
  }

  public Settlement latestForClaim(int claimId) {
    return settlements
        .findTopByClaimIdOrderBySettlementIdDesc(claimId)
        .orElseThrow(() -> new NotFoundException("settlement.notFound"));
  }

  public List<Settlement> allSettlements() {
    return settlements.findAll(Sort.by("settlementId"));
  }

  public List<Payment> allPayments() {
    return payments.findAll();
  }

  @Transactional
  public void reset() {
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-settlements.sql"))
        .execute(dataSource);
  }

  private double policyLimit(int claimId) {
    // legacy-faithful: a missing claim or policy silently falls back to a 10000 limit.
    return claims
        .findById(claimId)
        .flatMap(claim -> policies.findById(claim.getPolicyId()))
        .map(policy -> policy.getPolicyLimit().doubleValue())
        .orElse(DEFAULT_POLICY_LIMIT);
  }

  private static int integer(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }

  private static double decimal(String value, double fallback) {
    try {
      return Double.parseDouble(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }
}
