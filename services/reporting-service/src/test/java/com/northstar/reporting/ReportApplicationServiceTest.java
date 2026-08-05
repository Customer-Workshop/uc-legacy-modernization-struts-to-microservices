package com.northstar.reporting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.northstar.reporting.dto.AgedClaimsRow;
import com.northstar.reporting.dto.LossRatioRow;
import com.northstar.reporting.dto.OpenClaimsRow;
import com.northstar.reporting.repository.ReportRepository;
import com.northstar.reporting.service.ReportApplicationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReportApplicationServiceTest {
  private final ReportRepository repository = mock(ReportRepository.class);
  private final ReportApplicationService service =
      new ReportApplicationService(repository, "2019-04-01");

  @Test
  void openCountIsReadAsDoubleAndTruncatedToLong() {
    when(repository.openClaimsByAdjuster())
        .thenReturn(
            List.of(
                Map.of(
                    "assigned_adjuster",
                    "adjuster1",
                    "open_count",
                    24.0,
                    "reserve_total",
                    25404.0)));
    OpenClaimsRow row = service.openClaimsByAdjuster().get(0);
    assertThat(row.openCount()).isEqualTo(24L);
    assertThat(row.reserveTotal()).isEqualTo(25404.0);
  }

  @Test
  void zeroPremiumYieldsZeroLossRatioInsteadOfError() {
    when(repository.lossRatioByLine())
        .thenReturn(
            List.of(
                Map.of(
                    "line_of_business",
                    "AUTO",
                    "premium_total",
                    0.0,
                    "reserve_total",
                    100.0,
                    "paid_total",
                    50.0)));
    LossRatioRow row = service.lossRatioByLine().get(0);
    assertThat(row.lossRatio()).isEqualTo(0.0);
    assertThat(row.lossTotal()).isEqualTo(150.0);
  }

  @Test
  void lossRatioUsesDoubleDivision() {
    when(repository.lossRatioByLine())
        .thenReturn(
            List.of(
                Map.of(
                    "line_of_business",
                    "AUTO",
                    "premium_total",
                    91400.0,
                    "reserve_total",
                    40000.0,
                    "paid_total",
                    6270.0)));
    assertThat(service.lossRatioByLine().get(0).lossRatio()).isEqualTo(46270.0 / 91400.0);
  }

  @Test
  void agedReserveTotalsAreTruncatedNotRounded() {
    when(repository.agedClaims(any(LocalDate.class)))
        .thenReturn(
            List.of(Map.of("age_bucket", "0_30", "claim_count", 19.0, "reserve_total", 23586.75)));
    AgedClaimsRow row = service.agedClaims().get(0);
    assertThat(row.reserveTotal()).isEqualTo(23586L);
    assertThat(row.claimCount()).isEqualTo(19L);
  }

  @Test
  void agedBucketLabelsAreTrimmed() {
    when(repository.agedClaims(any(LocalDate.class)))
        .thenReturn(
            List.of(Map.of("age_bucket", "91_PLUS ", "claim_count", 18.0, "reserve_total", 0.0)));
    assertThat(service.agedClaims().get(0).bucket()).isEqualTo("91_PLUS");
  }
}
