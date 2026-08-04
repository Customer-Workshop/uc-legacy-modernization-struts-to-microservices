package com.northstar.settlement.service;

import com.northstar.settlement.dto.PaymentIssueRequest;
import com.northstar.settlement.dto.SettlementCalculateRequest;
import com.northstar.settlement.dto.SettlementSaveRequest;
import com.northstar.settlement.exception.NotFoundException;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Policy;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.ClaimRepository;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.PolicyRepository;
import com.northstar.settlement.repository.SettlementRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

@Service
public class SettlementApplicationService {
  private final ClaimRepository claims;
  private final PolicyRepository policies;
  private final SettlementRepository settlements;
  private final PaymentRepository payments;
  private final SettlementCalculator calculator;
  private final DataSource dataSource;

  public SettlementApplicationService(
      ClaimRepository claims,
      PolicyRepository policies,
      SettlementRepository settlements,
      PaymentRepository payments,
      SettlementCalculator calculator,
      DataSource dataSource) {
    this.claims = claims;
    this.policies = policies;
    this.settlements = settlements;
    this.payments = payments;
    this.calculator = calculator;
    this.dataSource = dataSource;
  }

  public int integer(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (Exception ignored) {
      // legacy-faithful: malformed or absent integer parameters use the action fallback.
      return fallback;
    }
  }

  public double decimal(String value, double fallback) {
    try {
      return Double.parseDouble(value);
    } catch (Exception ignored) {
      // legacy-faithful: malformed or absent decimal parameters use the action fallback.
      return fallback;
    }
  }

  public CalculatedSettlement calculate(SettlementCalculateRequest request) {
    int claimId = integer(request.claimId(), 119);
    double limit = policyLimitForCalculation(claimId);
    double covered = decimal(request.coveredAmount(), 5000);
    double depreciation = decimal(request.depreciation(), 0);
    return calculator.calculate(claimId, covered, request.deductible(), depreciation, limit);
  }

  @Transactional
  public Settlement save(SettlementSaveRequest request) {
    int claimId = integer(request.claimId(), 119);
    double limit = policyLimitForCalculation(claimId);
    double covered = decimal(request.coveredAmount(), 5000);
    double depreciation = decimal(request.depreciation(), 0);
    CalculatedSettlement calculated =
        calculator.calculate(claimId, covered, request.deductible(), depreciation, limit);
    String savedBy =
        request.savedBy() == null || request.savedBy().trim().isEmpty()
            ? "supervisor"
            : request.savedBy();
    // legacy-faithful: the session operator is represented by the optional savedBy request field.
    Settlement value =
        new Settlement(
            settlements.nextId(),
            claimId,
            calculated.coveredAmount(),
            calculated.deductibleApplied(),
            calculated.depreciation(),
            calculated.cappedAtLimit(),
            calculated.settlementAmount(),
            savedBy,
            LocalDate.of(2019, 4, 1));
    return settlements.save(value);
  }

  @Transactional
  public Payment issue(PaymentIssueRequest request) {
    int claimId = integer(request.claimId(), 119);
    Settlement settlement = latestSettlement(claimId);
    double amount = decimal(request.amount(), settlement.getSettlementAmount());
    int paymentId = payments.nextId();
    // legacy-faithful: payment IDs use an unpadded CHK- prefix, unlike seed data.
    Payment payment =
        new Payment(
            paymentId,
            claimId,
            settlement.getSettlementId(),
            request.payeeName(),
            amount,
            request.paymentMethod(),
            "CHK-" + paymentId,
            LocalDate.of(2019, 4, 3),
            "ISSUED");
    return payments.save(payment);
  }

  public Settlement latestSettlement(int claimId) {
    return settlements
        .findFirstByClaimIdOrderBySettlementIdDesc(claimId)
        .orElseThrow(() -> new NotFoundException("settlement.notFound"));
  }

  public Settlement detail(int claimId) {
    return latestSettlement(integer(String.valueOf(claimId), 119));
  }

  public List<Payment> paymentHistory(String claimId) {
    return payments.findByClaimIdOrderByPaymentIdAsc(integer(claimId, 119));
  }

  public List<Payment> remittance(String claimId) {
    return paymentHistory(claimId);
  }

  public Payment paymentDetail(int paymentId) {
    return payments
        .findById(integer(String.valueOf(paymentId), 61))
        .orElseThrow(() -> new NotFoundException("payment.notFound"));
  }

  public Map<String, Map<String, Object>> settlementClaims() {
    Map<String, Map<String, Object>> result = new LinkedHashMap<>();
    for (Settlement settlement : settlements.findAllByOrderBySettlementIdAsc()) {
      result.put(
          String.valueOf(settlement.getClaimId()),
          Map.of(
              "amount", settlement.getSettlementAmount(),
              "settlementId", settlement.getSettlementId(),
              "savedBy", settlement.getCalculatedBy()));
    }
    return result;
  }

  public Map<String, Map<String, Integer>> paymentCounts() {
    Map<String, Integer> result = new LinkedHashMap<>();
    for (Object[] row : payments.countByClaim()) {
      result.put(String.valueOf(row[0]), ((Number) row[1]).intValue());
    }
    return Map.of("claim", result);
  }

  public double paymentTotal(String claimId) {
    return payments.totalIssued(integer(claimId, 119));
  }

  @Transactional
  public void reset() {
    payments.deleteAllInBatch();
    settlements.deleteAllInBatch();
    claims.deleteAllInBatch();
    policies.deleteAllInBatch();
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-settlement.sql"))
        .execute(dataSource);
  }

  private double policyLimitForCalculation(int claimId) {
    return claims
        .findById(claimId)
        .flatMap(claim -> policies.findById(claim.getPolicyId()))
        .map(Policy::getPolicyLimit)
        .map(Number::doubleValue)
        .orElse(10000.0);
  }
}
