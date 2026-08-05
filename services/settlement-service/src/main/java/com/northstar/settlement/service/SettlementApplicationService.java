package com.northstar.settlement.service;

import com.northstar.settlement.dto.CalculationResponse;
import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.exception.NotFoundException;
import com.northstar.settlement.model.Claim;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.ClaimRepository;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.PolicyRepository;
import com.northstar.settlement.repository.SettlementRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementApplicationService {
  // legacy-faithful fallbacks from ClaimsActionSupport / the settlement and payment actions.
  private static final int DEFAULT_CLAIM_ID = 119;
  private static final int DEFAULT_PAYMENT_ID = 61;
  private static final double DEFAULT_COVERED_AMOUNT = 5000;
  private static final double DEFAULT_POLICY_LIMIT = 10000;
  private static final LocalDate SETTLEMENT_DATE = LocalDate.of(2019, 4, 1);
  private static final LocalDate PAYMENT_DATE = LocalDate.of(2019, 4, 3);
  private static final String OPERATOR = "supervisor";

  private final SettlementCalculator calculator;
  private final SettlementRepository settlements;
  private final PaymentRepository payments;
  private final ClaimRepository claims;
  private final PolicyRepository policies;
  private final DataSource dataSource;

  public SettlementApplicationService(
      SettlementCalculator calculator,
      SettlementRepository settlements,
      PaymentRepository payments,
      ClaimRepository claims,
      PolicyRepository policies,
      DataSource dataSource) {
    this.calculator = calculator;
    this.settlements = settlements;
    this.payments = payments;
    this.claims = claims;
    this.policies = policies;
    this.dataSource = dataSource;
  }

  public CalculationResponse calculate(SettlementRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    double limit = policyLimit(claimId);
    SettlementCalculator.Calculation result =
        calculator.calculate(
            decimal(request.coveredAmount(), DEFAULT_COVERED_AMOUNT),
            blankToZero(request.deductible()),
            decimal(request.depreciation(), 0),
            limit);
    return new CalculationResponse(
        claimId,
        money(result.coveredAmount()),
        money(result.deductibleApplied()),
        money(result.depreciation()),
        String.valueOf(result.cappedAtLimit()),
        money(result.settlementAmount()),
        money(limit));
  }

  @Transactional
  public Settlement save(SettlementRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    // legacy-faithful: SettlementSaveAction dereferences the claim and policy without null
    // checks, so a missing claim is a server error rather than a validation message.
    Claim claim =
        claims.findById(claimId).orElseThrow(() -> new IllegalStateException("claim missing"));
    double limit =
        policies
            .findById(claim.getPolicyId())
            .orElseThrow(() -> new IllegalStateException("policy missing"))
            .getPolicyLimit()
            .doubleValue();
    SettlementCalculator.Calculation result =
        calculator.calculate(
            decimal(request.coveredAmount(), DEFAULT_COVERED_AMOUNT),
            blankToZero(request.deductible()),
            decimal(request.depreciation(), 0),
            limit);
    Settlement value =
        new Settlement(
            settlements.nextId(),
            claimId,
            BigDecimal.valueOf(result.coveredAmount()),
            BigDecimal.valueOf(result.deductibleApplied()),
            BigDecimal.valueOf(result.depreciation()),
            result.cappedAtLimit(),
            BigDecimal.valueOf(result.settlementAmount()),
            OPERATOR,
            SETTLEMENT_DATE);
    return settlements.save(value);
  }

  @Transactional
  public Payment issuePayment(PaymentRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    Settlement settlement =
        settlements
            .findFirstByClaimIdOrderBySettlementIdDesc(claimId)
            .orElseThrow(() -> new IllegalStateException("settlement missing"));
    int paymentId = payments.nextId();
    double amount = decimal(request.amount(), settlement.getSettlementAmount().doubleValue());
    Payment payment =
        new Payment(
            paymentId,
            claimId,
            settlement.getSettlementId(),
            request.payeeName(),
            BigDecimal.valueOf(amount),
            request.paymentMethod(),
            "CHK-" + paymentId,
            PAYMENT_DATE,
            "ISSUED");
    return payments.save(payment);
  }

  public List<Payment> history(String claimId) {
    return payments.findByClaimIdOrderByPaymentIdAsc(integer(claimId, DEFAULT_CLAIM_ID));
  }

  public Payment paymentDetail(String paymentId) {
    return payments
        .findById(integer(paymentId, DEFAULT_PAYMENT_ID))
        .orElseThrow(() -> new NotFoundException("payment.notFound"));
  }

  public Settlement settlementDetail(String claimId) {
    return settlements
        .findFirstByClaimIdOrderBySettlementIdDesc(integer(claimId, DEFAULT_CLAIM_ID))
        .orElseThrow(() -> new NotFoundException("settlement.notFound"));
  }

  public Map<String, Settlement> latestByClaim() {
    Map<String, Settlement> latest = new LinkedHashMap<>();
    for (Settlement settlement : settlements.findAllByOrderBySettlementIdAsc()) {
      latest.put(String.valueOf(settlement.getClaimId()), settlement);
    }
    return latest;
  }

  public Map<String, Long> paymentCountsByClaim() {
    Map<String, Long> counts = new LinkedHashMap<>();
    for (Object[] row : payments.countByClaim()) {
      counts.put(String.valueOf(row[0]), (Long) row[1]);
    }
    return counts;
  }

  public double totalIssued(int claimId) {
    double total = 0;
    for (Payment payment : payments.findByClaimIdOrderByPaymentIdAsc(claimId)) {
      total += payment.getAmount().doubleValue();
    }
    return total;
  }

  @Transactional
  public void reset() {
    payments.deleteAllInBatch();
    settlements.deleteAllInBatch();
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-settlements.sql"))
        .execute(dataSource);
  }

  public int integer(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }

  public double decimal(String value, double fallback) {
    try {
      return Double.parseDouble(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }

  private String blankToZero(String deductible) {
    // legacy-faithful: the actions pass "0" for a blank deductible before calculation.
    return deductible == null || deductible.isEmpty() ? "0" : deductible;
  }

  private double policyLimit(int claimId) {
    // legacy-faithful: SettlementCalculateAction defaults the limit to 10000 when the claim or
    // policy cannot be loaded.
    return claims
        .findById(claimId)
        .flatMap(claim -> policies.findById(claim.getPolicyId()))
        .map(policy -> policy.getPolicyLimit().doubleValue())
        .orElse(DEFAULT_POLICY_LIMIT);
  }

  static String money(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
  }
}
