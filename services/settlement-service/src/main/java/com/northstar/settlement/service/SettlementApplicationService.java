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
  // legacy-faithful: unparseable or missing claimId falls back to claim 119.
  static final int DEFAULT_CLAIM_ID = 119;
  // legacy-faithful: blank coveredAmount defaults to 5000.
  static final double DEFAULT_COVERED_AMOUNT = 5000;
  // legacy-faithful: missing claim or policy falls back to a 10000 limit on calculate.
  static final double FALLBACK_POLICY_LIMIT = 10000;
  // legacy-faithful: saves record the recorded session user and a fixed audit date.
  static final String LEGACY_USER = "supervisor";
  static final LocalDate LEGACY_SAVE_DATE = LocalDate.of(2019, 4, 1);
  // legacy-faithful: payments are issued with a fixed date and ISSUED status.
  static final LocalDate LEGACY_ISSUE_DATE = LocalDate.of(2019, 4, 3);

  private final SettlementRepository settlements;
  private final PaymentRepository payments;
  private final PolicyRepository policies;
  private final ClaimRepository claims;
  private final DataSource dataSource;

  public SettlementApplicationService(
      SettlementRepository settlements,
      PaymentRepository payments,
      PolicyRepository policies,
      ClaimRepository claims,
      DataSource dataSource) {
    this.settlements = settlements;
    this.payments = payments;
    this.policies = policies;
    this.claims = claims;
    this.dataSource = dataSource;
  }

  public record CalculationResult(Settlement settlement, double policyLimit) {}

  public CalculationResult calculate(SettlementRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    double limit = FALLBACK_POLICY_LIMIT;
    Claim claim = claims.findById(claimId).orElse(null);
    if (claim != null) {
      Policy policy = policies.findById(claim.getPolicyId()).orElse(null);
      if (policy != null) {
        limit = policy.getPolicyLimit();
      }
    }
    LegacySettlementCalculator.Calculation value = run(request, limit);
    return new CalculationResult(
        new Settlement(
            null,
            claimId,
            value.coveredAmount(),
            value.deductibleApplied(),
            value.depreciation(),
            value.cappedAtLimit(),
            value.settlementAmount(),
            null,
            null),
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
    LegacySettlementCalculator.Calculation value = run(request, policy.getPolicyLimit());
    Settlement settlement =
        new Settlement(
            settlements.nextId(),
            claimId,
            value.coveredAmount(),
            value.deductibleApplied(),
            value.depreciation(),
            value.cappedAtLimit(),
            value.settlementAmount(),
            LEGACY_USER,
            LEGACY_SAVE_DATE);
    return settlements.save(settlement);
  }

  @Transactional
  public Payment issue(PaymentRequest request) {
    int claimId = integer(request.claimId(), DEFAULT_CLAIM_ID);
    Settlement settlement = settlements.findFirstByClaimIdOrderBySettlementIdDesc(claimId);
    if (settlement == null) {
      throw new NotFoundException("settlement.notFound");
    }
    int paymentId = payments.nextId();
    Payment payment =
        new Payment(
            paymentId,
            claimId,
            settlement.getSettlementId(),
            request.payeeName(),
            // legacy-faithful: a blank amount defaults to the latest settlement amount.
            decimal(request.amount(), settlement.getSettlementAmount()),
            request.paymentMethod(),
            // legacy-faithful: check numbers are derived from the allocated payment id.
            "CHK-" + paymentId,
            LEGACY_ISSUE_DATE,
            "ISSUED");
    return payments.save(payment);
  }

  public record History(int claimId, List<Payment> payments) {}

  public History history(String claimId) {
    int resolved = integer(claimId, DEFAULT_CLAIM_ID);
    return new History(resolved, payments.findByClaimIdOrderByPaymentId(resolved));
  }

  public Payment getPayment(int id) {
    return payments.findById(id).orElseThrow(() -> new NotFoundException("payment.notFound"));
  }

  public Map<String, Double> latestAmountsByClaim() {
    Map<String, Double> amounts = new LinkedHashMap<>();
    for (Settlement settlement : settlements.findLatestPerClaim()) {
      amounts.put(String.valueOf(settlement.getClaimId()), settlement.getSettlementAmount());
    }
    return amounts;
  }

  public Map<String, Long> paymentCountsByClaim() {
    Map<String, Long> counts = new LinkedHashMap<>();
    for (Object[] row : payments.countPerClaim()) {
      counts.put(String.valueOf(row[0]), (Long) row[1]);
    }
    return counts;
  }

  @Transactional
  public void reset() {
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-settlement.sql"))
        .execute(dataSource);
  }

  private LegacySettlementCalculator.Calculation run(SettlementRequest request, double limit) {
    double covered = decimal(request.coveredAmount(), DEFAULT_COVERED_AMOUNT);
    double depreciation = decimal(request.depreciation(), 0);
    String deductible = request.deductible();
    // legacy-faithful: a blank deductible is coerced to "0" before parsing.
    return LegacySettlementCalculator.calculate(
        covered,
        deductible == null || deductible.isEmpty() ? "0" : deductible,
        depreciation,
        limit);
  }

  static int integer(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }

  static double decimal(String value, double fallback) {
    try {
      return Double.parseDouble(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }
}
