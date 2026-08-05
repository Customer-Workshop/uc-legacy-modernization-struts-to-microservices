package com.northstar.workbench.service;

import com.northstar.workbench.exception.NotFoundException;
import com.northstar.workbench.model.Claim;
import com.northstar.workbench.repository.ClaimRepository;
import java.math.BigDecimal;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkbenchApplicationService {
  public static final int DEFAULT_CLAIM_ID = 119;
  public static final String DEFAULT_ADJUSTER = "adjuster2";
  public static final String DEFAULT_STATUS = "INVESTIGATING";
  public static final double DEFAULT_RESERVE = 4500;

  private final ClaimRepository repository;
  private final DataSource dataSource;

  public WorkbenchApplicationService(ClaimRepository repository, DataSource dataSource) {
    this.repository = repository;
    this.dataSource = dataSource;
  }

  @Transactional
  public Claim assign(String claimIdValue, String adjuster) {
    int claimId = integer(claimIdValue, DEFAULT_CLAIM_ID);
    if (adjuster == null || adjuster.isEmpty()) {
      // legacy-faithful: a missing or blank adjuster silently assigns adjuster2.
      adjuster = DEFAULT_ADJUSTER;
    }
    // legacy-faithful: the update succeeds with zero rows for unknown claims; no 404.
    repository.updateAdjuster(claimId, adjuster);
    return repository.findById(claimId).orElse(null);
  }

  @Transactional
  public Claim changeStatus(String claimIdValue, String status) {
    int claimId = integer(claimIdValue, DEFAULT_CLAIM_ID);
    if (status == null || status.isEmpty()) {
      // legacy-faithful: a missing or blank status silently becomes INVESTIGATING.
      status = DEFAULT_STATUS;
    }
    // legacy-faithful: any status string is accepted without validation.
    repository.updateStatus(claimId, status);
    return repository.findById(claimId).orElse(null);
  }

  @Transactional
  public double changeReserve(String claimIdValue, String reserveValue) {
    int claimId = integer(claimIdValue, DEFAULT_CLAIM_ID);
    double reserve = decimal(reserveValue, DEFAULT_RESERVE);
    repository.updateReserve(claimId, BigDecimal.valueOf(reserve));
    return reserve;
  }

  public Claim find(String claimIdValue) {
    return repository.findById(integer(claimIdValue, DEFAULT_CLAIM_ID)).orElse(null);
  }

  public Claim get(int claimId) {
    return repository.findById(claimId).orElseThrow(() -> new NotFoundException("claim.notFound"));
  }

  @Transactional
  public void reset() {
    repository.deleteAllInBatch();
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-claims.sql")).execute(dataSource);
  }

  int integer(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (RuntimeException failure) {
      // legacy-faithful: unparseable claim ids fall back to claim 119.
      return fallback;
    }
  }

  double decimal(String value, double fallback) {
    try {
      return Double.parseDouble(value);
    } catch (RuntimeException failure) {
      // legacy-faithful: unparseable reserve amounts fall back to 4500.
      return fallback;
    }
  }
}
