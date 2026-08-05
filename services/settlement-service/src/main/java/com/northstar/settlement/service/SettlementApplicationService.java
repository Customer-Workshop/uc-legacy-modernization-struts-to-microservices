package com.northstar.settlement.service;

import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.model.Claim;
import com.northstar.settlement.model.Policy;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.ClaimRepository;
import com.northstar.settlement.repository.PolicyRepository;
import com.northstar.settlement.repository.SettlementRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementApplicationService {
  private final SettlementCalculator calculator;
  private final SettlementRepository settlements;
  private final ClaimRepository claims;
  private final PolicyRepository policies;
  private final DataSource dataSource;

  public SettlementApplicationService(
      SettlementCalculator calculator,
      SettlementRepository settlements,
      ClaimRepository claims,
      PolicyRepository policies,
      DataSource dataSource) {
    this.calculator = calculator;
    this.settlements = settlements;
    this.claims = claims;
    this.policies = policies;
    this.dataSource = dataSource;
  }

  public Settlement calculate(SettlementRequest request) {
    // legacy-faithful: SettlementCalculateAction defaults claimId 119 and covered 5000.
    int claimId = LegacyCoercions.integer(request.claimId(), 119);
    double limit = policyLimitOrDefault(claimId);
    double covered = LegacyCoercions.decimal(request.coveredAmount(), 5000);
    double depreciation = LegacyCoercions.decimal(request.depreciation(), 0);
    String deductible = request.deductible();
    Settlement settlement =
        calculator.calculate(
            covered,
            deductible == null || deductible.length() == 0 ? "0" : deductible,
            depreciation,
            limit);
    settlement.setClaimId(claimId);
    return settlement;
  }

  @Transactional
  public Settlement save(SettlementRequest request, String user) {
    int claimId = LegacyCoercions.integer(request.claimId(), 119);
    Claim claim = claims.findById(claimId).orElseThrow(() -> new IllegalStateException("claim"));
    Policy policy =
        policies
            .findById(claim.getPolicyId())
            .orElseThrow(() -> new IllegalStateException("policy"));
    double covered = LegacyCoercions.decimal(request.coveredAmount(), 5000);
    double depreciation = LegacyCoercions.decimal(request.depreciation(), 0);
    String deductible = request.deductible();
    Settlement value =
        calculator.calculate(
            covered,
            deductible == null || deductible.length() == 0 ? "0" : deductible,
            depreciation,
            policy.getPolicyLimit());
    value.setSettlementId(settlements.nextId());
    value.setClaimId(claimId);
    value.setCalculatedBy(user);
    // legacy-faithful: SettlementSaveAction stamps the fixed date 2019-04-01.
    value.setCalculatedDate("2019-04-01");
    return settlements.save(value);
  }

  public Map<String, Map<String, Object>> latestByClaim() {
    Map<String, Map<String, Object>> byClaim = new LinkedHashMap<>();
    for (Settlement value : settlements.findAllByOrderBySettlementIdAsc()) {
      Map<String, Object> fields = new LinkedHashMap<>();
      fields.put("settlementId", value.getSettlementId());
      fields.put("settlementAmount", value.getSettlementAmount());
      fields.put("cappedAtLimit", String.valueOf(value.isCappedAtLimit()));
      byClaim.put(String.valueOf(value.getClaimId()), fields);
    }
    return byClaim;
  }

  @Transactional
  public void reset() {
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-settlements.sql"))
        .execute(dataSource);
  }

  private double policyLimitOrDefault(int claimId) {
    // legacy-faithful: a missing claim or policy falls back to a 10000 limit.
    return claims
        .findById(claimId)
        .flatMap(claim -> policies.findById(claim.getPolicyId()))
        .map(Policy::getPolicyLimit)
        .orElse(10000.0);
  }
}
