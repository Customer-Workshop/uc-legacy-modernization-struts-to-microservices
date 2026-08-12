package com.northstar.settlement.service;

import com.northstar.settlement.dto.PaymentIssueRequest;
import com.northstar.settlement.dto.SettlementCalcRequest;
import com.northstar.settlement.exception.NotFoundException;
import com.northstar.settlement.model.ClaimRef;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.PolicyRef;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.ClaimRefRepository;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.PolicyRefRepository;
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
  // legacy-faithful defaults from SettlementCalculateAction/PaymentIssueAction.
  static final int DEFAULT_CLAIM_ID = 119;
  static final double DEFAULT_COVERED = 5000;
  static final double DEFAULT_POLICY_LIMIT = 10000;
  static final String OPERATOR = "supervisor";
  static final LocalDate SAVE_DATE = LocalDate.of(2019, 4, 1);
  static final LocalDate ISSUE_DATE = LocalDate.of(2019, 4, 3);

  private final SettlementRepository settlements;
  private final PaymentRepository payments;
  private final ClaimRefRepository claims;
  private final PolicyRefRepository policies;
  private final DataSource dataSource;

  public SettlementApplicationService(
      SettlementRepository settlements,
      PaymentRepository payments,
      ClaimRefRepository claims,
      PolicyRefRepository policies,
      DataSource dataSource) {
    this.settlements = settlements;
    this.payments = payments;
    this.claims = claims;
    this.policies = policies;
    this.dataSource = dataSource;
  }

  public SettlementCalculator.Result calculate(SettlementCalcRequest request) {
    int claimId = LegacyCoercions.integer(request.claimId(), DEFAULT_CLAIM_ID);
    return SettlementCalculator.calculate(
        LegacyCoercions.decimal(request.coveredAmount(), DEFAULT_COVERED),
        request.deductible() == null || request.deductible().isEmpty() ? "0" : request.deductible(),
        LegacyCoercions.decimal(request.depreciation(), 0),
        policyLimit(claimId, DEFAULT_POLICY_LIMIT));
  }

  @Transactional
  public Settlement save(SettlementCalcRequest request) {
    int claimId = LegacyCoercions.integer(request.claimId(), DEFAULT_CLAIM_ID);
    ClaimRef claim =
        claims.findById(claimId).orElseThrow(() -> new NotFoundException("claim.notFound"));
    PolicyRef policy =
        policies
            .findById(claim.getPolicyId())
            .orElseThrow(() -> new NotFoundException("policy.notFound"));
    SettlementCalculator.Result result =
        SettlementCalculator.calculate(
            LegacyCoercions.decimal(request.coveredAmount(), DEFAULT_COVERED),
            request.deductible() == null || request.deductible().isEmpty()
                ? "0"
                : request.deductible(),
            LegacyCoercions.decimal(request.depreciation(), 0),
            policy.getPolicyLimit());
    Settlement value =
        new Settlement(
            settlements.nextId(),
            claimId,
            result.coveredAmount(),
            result.deductibleApplied(),
            result.depreciation(),
            result.cappedAtLimit(),
            result.settlementAmount(),
            OPERATOR,
            SAVE_DATE);
    return settlements.save(value);
  }

  @Transactional
  public Payment issue(PaymentIssueRequest request) {
    int claimId = LegacyCoercions.integer(request.claimId(), DEFAULT_CLAIM_ID);
    Settlement settlement =
        settlements
            .findFirstByClaimIdOrderBySettlementIdDesc(claimId)
            .orElseThrow(() -> new NotFoundException("settlement.notFound"));
    int paymentId = payments.nextId();
    Payment payment =
        new Payment(
            paymentId,
            claimId,
            settlement.getSettlementId(),
            request.payeeName(),
            LegacyCoercions.decimal(request.amount(), settlement.getSettlementAmount()),
            request.paymentMethod(),
            "CHK-" + paymentId,
            ISSUE_DATE,
            "ISSUED");
    return payments.save(payment);
  }

  public List<Payment> history(String claimId) {
    return payments.findByClaimIdOrderByPaymentIdAsc(
        LegacyCoercions.integer(claimId, DEFAULT_CLAIM_ID));
  }

  public Payment payment(int id) {
    return payments.findById(id).orElseThrow(() -> new NotFoundException("payment.notFound"));
  }

  public Settlement settlement(int id) {
    return settlements.findById(id).orElseThrow(() -> new NotFoundException("settlement.notFound"));
  }

  public Map<String, Map<String, String>> latestAmountsByClaim() {
    Map<String, Map<String, String>> byClaim = new LinkedHashMap<>();
    for (Settlement s : settlements.findAllByOrderBySettlementIdAsc()) {
      byClaim.put(
          s.getClaimId().toString(), Map.of("amount", LegacyMoney.format(s.getSettlementAmount())));
    }
    return byClaim;
  }

  public Map<String, Map<String, Long>> countsByClaim() {
    Map<String, Long> counts = new LinkedHashMap<>();
    for (Payment p : payments.findAll()) {
      counts.merge(p.getClaimId().toString(), 1L, Long::sum);
    }
    return Map.of("claim", counts);
  }

  private double policyLimit(int claimId, double fallback) {
    return claims
        .findById(claimId)
        .flatMap(claim -> policies.findById(claim.getPolicyId()))
        .map(PolicyRef::getPolicyLimit)
        .orElse(fallback);
  }

  @Transactional
  public void reset() {
    payments.deleteAllInBatch();
    settlements.deleteAllInBatch();
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-settlements.sql"))
        .execute(dataSource);
  }
}
