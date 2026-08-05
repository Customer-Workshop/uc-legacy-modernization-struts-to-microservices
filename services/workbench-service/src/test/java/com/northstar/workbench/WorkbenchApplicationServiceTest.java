package com.northstar.workbench;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.northstar.workbench.controller.WorkbenchController;
import com.northstar.workbench.model.Claim;
import com.northstar.workbench.model.ClaimNote;
import com.northstar.workbench.repository.ClaimNoteRepository;
import com.northstar.workbench.repository.ClaimRepository;
import com.northstar.workbench.service.WorkbenchApplicationService;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkbenchApplicationServiceTest {
  private ClaimRepository claims;
  private ClaimNoteRepository notes;
  private WorkbenchApplicationService service;

  @BeforeEach
  void setUp() {
    claims = mock(ClaimRepository.class);
    notes = mock(ClaimNoteRepository.class);
    service = new WorkbenchApplicationService(claims, notes, mock(DataSource.class));
  }

  @Test
  void quirkInvalidClaimIdFallsBackToClaim119() {
    assertThat(service.claimId(null)).isEqualTo(119);
    assertThat(service.claimId("")).isEqualTo(119);
    assertThat(service.claimId("abc")).isEqualTo(119);
    assertThat(service.claimId("121")).isEqualTo(121);
  }

  @Test
  void quirkBlankAdjusterDefaultsToAdjuster2() {
    when(claims.findById(121)).thenReturn(Optional.empty());
    assertThat(service.assign(121, null)).isEqualTo("adjuster2");
    assertThat(service.assign(121, "")).isEqualTo("adjuster2");
    assertThat(service.assign(121, "adjuster7")).isEqualTo("adjuster7");
  }

  @Test
  void quirkBlankStatusDefaultsToInvestigating() {
    when(claims.findById(121)).thenReturn(Optional.empty());
    assertThat(service.changeStatus(121, null)).isEqualTo("INVESTIGATING");
    assertThat(service.changeStatus(121, "")).isEqualTo("INVESTIGATING");
    assertThat(service.changeStatus(121, "CLOSED")).isEqualTo("CLOSED");
  }

  @Test
  void quirkUnparseableReserveDefaultsTo4500DoubleArithmetic() {
    assertThat(service.reserve(null)).isEqualTo(4500d);
    assertThat(service.reserve("")).isEqualTo(4500d);
    assertThat(service.reserve("not-a-number")).isEqualTo(4500d);
    assertThat(service.reserve("4500.00")).isEqualTo(4500d);
  }

  @Test
  void quirkUpdateOnMissingClaimIsSilentNoOp() {
    when(claims.findById(999)).thenReturn(Optional.empty());
    assertThat(service.assign(999, "adjuster3")).isEqualTo("adjuster3");
    assertThat(service.changeStatus(999, "CLOSED")).isEqualTo("CLOSED");
    assertThat(service.changeReserve(999, "10.00")).isEqualTo(10d);
    verify(claims, never()).save(any(Claim.class));
  }

  @Test
  void quirkBlankNoteDefaultsAndIsNeverPersisted() {
    assertThat(service.noteText(null)).isEqualTo("Review completed");
    assertThat(service.noteText("")).isEqualTo("Review completed");
    assertThat(service.noteText("custom")).isEqualTo("custom");
    verify(notes, never()).save(any(ClaimNote.class));
  }

  @Test
  void quirkStatusAndReserveHistoryAreAlwaysEmpty() {
    var controller = new WorkbenchController(service);
    assertThat(controller.statusHistory("121").entries()).isEmpty();
    assertThat(controller.reserveHistory("121").entries()).isEmpty();
  }
}
