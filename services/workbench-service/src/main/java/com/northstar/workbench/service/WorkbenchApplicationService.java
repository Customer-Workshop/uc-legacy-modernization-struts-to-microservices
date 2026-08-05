package com.northstar.workbench.service;

import com.northstar.workbench.model.Claim;
import com.northstar.workbench.model.ClaimNote;
import com.northstar.workbench.repository.ClaimNoteRepository;
import com.northstar.workbench.repository.ClaimRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkbenchApplicationService {
  public static final int FALLBACK_CLAIM_ID = 119;
  public static final String DEFAULT_ADJUSTER = "adjuster2";
  public static final String DEFAULT_STATUS = "INVESTIGATING";
  public static final double DEFAULT_RESERVE = 4500;
  public static final String DEFAULT_NOTE = "Review completed";

  private final ClaimRepository claims;
  private final ClaimNoteRepository notes;
  private final DataSource dataSource;

  public WorkbenchApplicationService(
      ClaimRepository claims, ClaimNoteRepository notes, DataSource dataSource) {
    this.claims = claims;
    this.notes = notes;
    this.dataSource = dataSource;
  }

  // legacy-faithful: ClaimsActionSupport.integer falls back to claim 119 on any parse failure.
  public int claimId(String raw) {
    try {
      return Integer.parseInt(raw);
    } catch (Exception failure) {
      return FALLBACK_CLAIM_ID;
    }
  }

  // legacy-faithful: ClaimsActionSupport.decimal falls back to 4500 on any parse failure.
  public double reserve(String raw) {
    try {
      return Double.parseDouble(raw);
    } catch (Exception failure) {
      return DEFAULT_RESERVE;
    }
  }

  public List<Claim> openClaims() {
    return claims.findByStatusOrderByClaimId("OPEN");
  }

  public Optional<Claim> find(int claimId) {
    return claims.findById(claimId);
  }

  @Transactional
  public String assign(int claimId, String adjuster) {
    // legacy-faithful: blank adjuster defaults to adjuster2.
    String effective = (adjuster == null || adjuster.isEmpty()) ? DEFAULT_ADJUSTER : adjuster;
    // Sanctioned change: the legacy Action concatenated this UPDATE; JPA parameterizes it.
    claims
        .findById(claimId)
        .ifPresent(
            claim -> {
              claim.setAssignedAdjuster(effective);
              claims.save(claim);
            });
    return effective;
  }

  @Transactional
  public String changeStatus(int claimId, String status) {
    // legacy-faithful: blank status defaults to INVESTIGATING.
    String effective = (status == null || status.isEmpty()) ? DEFAULT_STATUS : status;
    claims
        .findById(claimId)
        .ifPresent(
            claim -> {
              claim.setStatus(effective);
              claims.save(claim);
            });
    return effective;
  }

  @Transactional
  public double changeReserve(int claimId, String rawAmount) {
    // legacy-faithful: reserve stays double arithmetic, defaulting to 4500.
    double effective = reserve(rawAmount);
    claims
        .findById(claimId)
        .ifPresent(
            claim -> {
              claim.setReserveAmount(BigDecimal.valueOf(effective));
              claims.save(claim);
            });
    return effective;
  }

  public String noteText(String raw) {
    // legacy-faithful: blank note text defaults to "Review completed"; the legacy Action
    // never persists the note, so neither does this service.
    return (raw == null || raw.isEmpty()) ? DEFAULT_NOTE : raw;
  }

  public List<ClaimNote> noteHistory(int claimId) {
    return notes.findByClaimIdOrderByNoteDate(claimId);
  }

  @Transactional
  public void reset() {
    notes.deleteAllInBatch();
    claims.deleteAllInBatch();
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-workbench.sql"))
        .execute(dataSource);
  }
}
