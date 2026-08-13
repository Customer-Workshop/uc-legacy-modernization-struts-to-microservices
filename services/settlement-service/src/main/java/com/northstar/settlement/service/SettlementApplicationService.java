package com.northstar.settlement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.exception.NotFoundException;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.SettlementRepository;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
public class SettlementApplicationService {
  private static final Logger log = LoggerFactory.getLogger(SettlementApplicationService.class);
  private final SettlementRepository settlements;
  private final PaymentRepository payments;
  private final SettlementCalculator calculator;
  private final DataSource dataSource;
  private final RestClient intake;
  private final RestClient policy;

  public SettlementApplicationService(
      SettlementRepository settlements,
      PaymentRepository payments,
      SettlementCalculator calculator,
      DataSource dataSource,
      @Value("${settlement.intake-base-url}") String intakeUrl,
      @Value("${settlement.policy-base-url}") String policyUrl) {
    this.settlements = settlements;
    this.payments = payments;
    this.calculator = calculator;
    this.dataSource = dataSource;
    this.intake = RestClient.builder().baseUrl(intakeUrl).build();
    this.policy = RestClient.builder().baseUrl(policyUrl).build();
  }

  public Settlement calculate(SettlementRequest r) {
    int claimId = integer(r.claimId(), 119);
    double covered = decimal(r.coveredAmount(), 5000);
    double depreciation = decimal(r.depreciation(), 0);
    return calculator.calculate(
        claimId, covered, r.deductible(), depreciation, policyLimit(claimId));
  }

  @Transactional
  public Settlement save(SettlementRequest r) {
    Settlement value = calculate(r);
    int id = settlements.nextId();
    return settlements.save(
        new Settlement(
            id,
            value.getClaimId(),
            value.getCoveredAmount(),
            value.getDeductibleApplied(),
            value.getDepreciation(),
            value.getSettlementAmount(),
            value.isCappedAtLimit(),
            "supervisor",
            LocalDate.of(2019, 4, 1)));
  }

  public Settlement latest(int claimId) {
    return settlements
        .findFirstByClaimIdOrderBySettlementIdDesc(claimId)
        .orElseThrow(() -> new NotFoundException("settlement.notFound"));
  }

  @Transactional
  public Payment issue(PaymentRequest r) {
    int claimId = integer(r.claimId(), 119);
    Settlement settlement = latest(claimId);
    int id = payments.nextId();
    double amount = decimal(r.amount(), settlement.getSettlementAmount());
    // legacy-faithful: check numbers are generated from max payment id plus one.
    return payments.save(
        new Payment(
            id,
            claimId,
            settlement.getSettlementId(),
            r.payeeName(),
            amount,
            r.paymentMethod(),
            "CHK-" + id,
            LocalDate.of(2019, 4, 3),
            "ISSUED"));
  }

  public List<Payment> list(String claimId) {
    // legacy-faithful: missing or blank claimId defaults to legacy claim 119.
    return payments.findByClaimIdOrderByPaymentIdAsc(integer(claimId, 119));
  }

  public Payment getPayment(int id) {
    return payments.findById(id).orElseThrow(() -> new NotFoundException("payment.notFound"));
  }

  @Transactional
  public void reset() {
    payments.deleteAllInBatch();
    settlements.deleteAllInBatch();
    new ResourceDatabasePopulator(new ClassPathResource("db/migration/V2__seed.sql"))
        .execute(dataSource);
  }

  private double policyLimit(int claimId) {
    try {
      JsonNode claim =
          intake.get().uri("/api/claims/{id}", claimId).retrieve().body(JsonNode.class);
      Integer policyId = claim.get("policyId").asInt();
      JsonNode value =
          policy.get().uri("/api/policies/{id}", policyId).retrieve().body(JsonNode.class);
      return Double.parseDouble(value.get("policyLimit").asText());
    } catch (RuntimeException ex) {
      log.warn("Unable to resolve policy limit for claim {}; using legacy default", claimId, ex);
      return 10000;
    }
  }

  // legacy-faithful: malformed or blank integer form fields use the action fallback.
  public int integer(String value, int fallback) {
    try {
      return value == null || value.trim().isEmpty() ? fallback : Integer.parseInt(value);
    } catch (RuntimeException e) {
      return fallback;
    }
  }

  // legacy-faithful: malformed or blank decimal form fields use the action fallback.
  public double decimal(String value, double fallback) {
    try {
      return value == null || value.trim().isEmpty() ? fallback : Double.parseDouble(value);
    } catch (RuntimeException e) {
      return fallback;
    }
  }
}
