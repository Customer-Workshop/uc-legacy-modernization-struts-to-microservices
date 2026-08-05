package com.northstar.workbench;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.northstar.workbench.repository.ClaimRepository;
import com.northstar.workbench.service.WorkbenchApplicationService;
import java.math.BigDecimal;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class WorkbenchApplicationServiceTest {
  private final ClaimRepository repository = mock(ClaimRepository.class);
  private final WorkbenchApplicationService service =
      new WorkbenchApplicationService(repository, mock(DataSource.class));

  @Test
  void blankOrMissingAdjusterDefaultsToAdjuster2() {
    service.assign("121", null);
    verify(repository).updateAdjuster(121, "adjuster2");
    service.assign("121", "");
    verify(repository, org.mockito.Mockito.times(2)).updateAdjuster(121, "adjuster2");
  }

  @Test
  void providedAdjusterIsUsedVerbatim() {
    service.assign("121", "adjuster5");
    verify(repository).updateAdjuster(121, "adjuster5");
  }

  @Test
  void blankOrMissingStatusDefaultsToInvestigating() {
    service.changeStatus("121", null);
    verify(repository).updateStatus(121, "INVESTIGATING");
  }

  @Test
  void statusIsNotValidatedOrNormalized() {
    service.changeStatus("121", "definitely-not-a-status");
    verify(repository).updateStatus(121, "definitely-not-a-status");
  }

  @Test
  void unparseableClaimIdFallsBackToClaim119() {
    service.changeStatus("not-a-number", "OPEN");
    verify(repository).updateStatus(119, "OPEN");
    service.changeStatus(null, "OPEN");
    verify(repository, org.mockito.Mockito.times(2)).updateStatus(119, "OPEN");
  }

  @Test
  void unparseableReserveFallsBackTo4500() {
    double reserve = service.changeReserve("121", "");
    assertThat(reserve).isEqualTo(4500.0);
    verify(repository).updateReserve(eq(121), eq(BigDecimal.valueOf(4500.0)));
  }

  @Test
  void reserveUsesLegacyDoubleParsing() {
    double reserve = service.changeReserve("121", "4500.00");
    assertThat(reserve).isEqualTo(4500.0);
    verify(repository).updateReserve(eq(121), eq(BigDecimal.valueOf(4500.0)));
  }

  @Test
  void reserveRendersTwoDecimalsWithHalfUpRounding() {
    var claim =
        new com.northstar.workbench.model.Claim(
            121,
            "CLM-00121",
            9001,
            "Claimant 121",
            java.time.LocalDate.of(2019, 4, 1),
            java.time.LocalDate.of(2019, 4, 1),
            "WATER",
            "desc",
            "OPEN",
            new BigDecimal("1234.5670"),
            "adjuster1",
            "supervisor",
            java.time.LocalDate.of(2019, 4, 1));
    assertThat(com.northstar.workbench.dto.ClaimResponse.from(claim).reserveAmount())
        .isEqualTo("1234.57");
  }

  @Test
  void mutationsOnUnknownClaimsSucceedSilently() {
    assertThat(service.assign("999999", "adjuster1")).isNull();
    verify(repository).updateAdjuster(999999, "adjuster1");
  }
}
